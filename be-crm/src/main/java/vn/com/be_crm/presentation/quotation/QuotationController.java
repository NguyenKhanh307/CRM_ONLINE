package vn.com.be_crm.presentation.quotation;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.quotation.command.*;
import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.application.quotation.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
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

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách */
    public QuotationController(CreateQuotationUseCase createUC, UpdateQuotationUseCase updateUC,
                                DeleteQuotationUseCase deleteUC, GetQuotationUseCase getUC, ListQuotationUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới báo giá. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<QuotationResult>> create(@Valid @RequestBody CreateQuotationCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách báo giá. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<QuotationResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
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

    /** Xóa mềm báo giá. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
