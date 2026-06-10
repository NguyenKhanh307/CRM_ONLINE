package vn.com.be_crm.presentation.quotation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.quotation.command.*;
import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.application.quotation.query.*;
import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý báo giá.
 */
@RestController
@RequestMapping("/api/quotations")
public class QuotationController {
    private final CreateQuotationUseCase createUC;
    private final UpdateQuotationUseCase updateUC;
    private final DeleteQuotationUseCase deleteUC;
    private final GetQuotationUseCase getUC;
    private final ListQuotationUseCase listUC;
    private final ListDeletedQuotationsUseCase listDeletedUC;
    private final RestoreQuotationUseCase restoreUC;
    private final PurgeQuotationUseCase purgeUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn */
    public QuotationController(CreateQuotationUseCase createUC, UpdateQuotationUseCase updateUC,
                                DeleteQuotationUseCase deleteUC, GetQuotationUseCase getUC, ListQuotationUseCase listUC,
                                ListDeletedQuotationsUseCase listDeletedUC, RestoreQuotationUseCase restoreUC, PurgeQuotationUseCase purgeUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
    }

    /** Tạo mới báo giá. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<QuotationResult>> create(@Valid @RequestBody CreateQuotationCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách báo giá. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<QuotationResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).build()))));
    }

    /** Lấy báo giá theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuotationResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật báo giá. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuotationResult>> update(@PathVariable Long id,
                                                                @Valid @RequestBody UpdateQuotationCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateQuotationCommand.builder().id(id).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                        .ownerId(cmd.getOwnerId()).quoteDate(cmd.getQuoteDate()).validUntil(cmd.getValidUntil())
                        .status(cmd.getStatus()).subtotal(cmd.getSubtotal()).discount(cmd.getDiscount())
                        .tax(cmd.getTax()).total(cmd.getTotal()).note(cmd.getNote()).build())));
    }

    /** Xóa mềm báo giá. @param id ID @param req HTTP request @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Lấy danh sách báo giá trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Khôi phục báo giá từ thùng rác. @param id ID @return 200 */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn báo giá khỏi thùng rác. @param id ID @return 200 */
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
