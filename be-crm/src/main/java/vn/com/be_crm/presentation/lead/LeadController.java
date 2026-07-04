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
import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.lead.request.LeadActionRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.HandoverBulkRequest;
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
    private final ImportBulkLeadUseCase importBulkUC;
    private final HandoverBulkLeadUseCase handoverBulkUC;
    private final LeadWorkflowUseCase workflowUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     *  @param handoverBulkUC bàn giao hàng loạt @param workflowUC luồng trạng thái tiềm năng */
    public LeadController(CreateLeadUseCase createUC, UpdateLeadUseCase updateUC, DeleteLeadUseCase deleteUC,
                           GetLeadUseCase getUC, ListLeadUseCase listUC,
                           ListDeletedLeadsUseCase listDeletedUC, RestoreLeadUseCase restoreUC, PurgeLeadUseCase purgeUC,
                           ImportBulkLeadUseCase importBulkUC, HandoverBulkLeadUseCase handoverBulkUC,
                           LeadWorkflowUseCase workflowUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC; this.handoverBulkUC = handoverBulkUC; this.workflowUC = workflowUC;
    }

    /** Chuyển đổi tiềm năng (qualified → converted). @param id ID @return 200 */
    @PostMapping("/{id}/convert")
    public ResponseEntity<ApiResponse<LeadResult>> convert(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.convert(id)));
    }

    /** Đánh mất tiềm năng (→ lost). @param id ID @param body lý do @return 200 */
    @PostMapping("/{id}/lose")
    public ResponseEntity<ApiResponse<LeadResult>> lose(@PathVariable Long id,
            @RequestBody(required = false) LeadActionRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.lose(id, body != null ? body.getReason() : null)));
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
                UpdateLeadCommand.builder().id(id).name(cmd.getName())
                        .companyName(cmd.getCompanyName()).leadType(cmd.getLeadType())
                        .ownerId(cmd.getOwnerId())
                        .customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                        .title(cmd.getTitle()).department(cmd.getDepartment())
                        .taxCode(cmd.getTaxCode()).website(cmd.getWebsite()).industry(cmd.getIndustry())
                        .source(cmd.getSource()).campaignId(cmd.getCampaignId())
                        .estimatedValue(cmd.getEstimatedValue()).phone(cmd.getPhone())
                        .email(cmd.getEmail())
                        .doNotCall(cmd.getDoNotCall()).doNotEmail(cmd.getDoNotEmail())
                        .note(cmd.getNote()).build())));
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

    /** Nhập hàng loạt tiềm năng từ file. @param cmd body @return 200 */
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkLeadCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }

    /** Bàn giao hàng loạt tiềm năng sang người dùng khác. @param body body @param req HTTP request @return 200 */
    @PostMapping("/handover-bulk")
    public ResponseEntity<ApiResponse<Void>> handoverBulk(@Valid @RequestBody HandoverBulkRequest body, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdminOrManager = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        handoverBulkUC.execute(HandoverBulkCommand.builder()
                .ids(body.getIds()).toUserId(body.getToUserId())
                .currentUserId(userId).adminOrManager(isAdminOrManager)
                .reason(body.getReason()).build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
