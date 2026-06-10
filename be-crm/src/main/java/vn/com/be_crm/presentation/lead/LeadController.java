package vn.com.be_crm.presentation.lead;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.application.lead.query.*;
import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý tiềm năng bán hàng.
 */
@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private final CreateLeadUseCase createUC;
    private final UpdateLeadUseCase updateUC;
    private final DeleteLeadUseCase deleteUC;
    private final GetLeadUseCase getUC;
    private final ListLeadUseCase listUC;
    private final ListDeletedLeadsUseCase listDeletedUC;
    private final RestoreLeadUseCase restoreUC;
    private final PurgeLeadUseCase purgeUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn */
    public LeadController(CreateLeadUseCase createUC, UpdateLeadUseCase updateUC, DeleteLeadUseCase deleteUC,
                           GetLeadUseCase getUC, ListLeadUseCase listUC,
                           ListDeletedLeadsUseCase listDeletedUC, RestoreLeadUseCase restoreUC, PurgeLeadUseCase purgeUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
    }

    /** Tạo mới tiềm năng. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResult>> create(@Valid @RequestBody CreateLeadCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách tiềm năng. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LeadResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).build()))));
    }

    /** Lấy tiềm năng theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật tiềm năng. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateLeadCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateLeadCommand.builder().id(id).name(cmd.getName()).ownerId(cmd.getOwnerId())
                        .customerId(cmd.getCustomerId()).contactId(cmd.getContactId()).source(cmd.getSource())
                        .status(cmd.getStatus()).estimatedValue(cmd.getEstimatedValue()).phone(cmd.getPhone())
                        .email(cmd.getEmail()).note(cmd.getNote()).build())));
    }

    /** Xóa mềm tiềm năng. @param id ID @param req HTTP request @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Lấy danh sách tiềm năng trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Khôi phục tiềm năng từ thùng rác. @param id ID @return 200 */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn tiềm năng khỏi thùng rác. @param id ID @return 200 */
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
