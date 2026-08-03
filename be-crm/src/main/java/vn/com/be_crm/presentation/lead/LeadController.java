package vn.com.be_crm.presentation.lead;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.application.lead.query.*;
import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.dto.handover.HandoverBulkCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.presentation.lead.request.ConvertLeadRequest;
import vn.com.be_crm.presentation.lead.request.LeadActionRequest;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.dto.handover.HandoverBulkRequest;
import vn.com.be_crm.core.page.PageResponse;

// REST controller cho nghiệp vụ quản lý tiềm năng bán hàng
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

    // body tùy chọn { customerId?, contactId? }: chỉ định khách hàng/liên hệ đã có để dùng lại
    // (chống tạo trùng khi phát hiện bản ghi trùng MST/email/SĐT); bỏ trống -> tạo mới như cũ
    @PreAuthorize("hasAuthority('lead.convert')")
    @PostMapping("/{id}/convert")
    public ResponseEntity<ApiResponse<LeadResult>> convert(@PathVariable Long id,
            @RequestBody(required = false) ConvertLeadRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.convert(id,
                body != null ? body.getCustomerId() : null,
                body != null ? body.getContactId() : null)));
    }

    // đủ điều kiện thủ công (new/contacting -> qualified) — không cần đủ 50 điểm
    @PostMapping("/{id}/qualify")
    public ResponseEntity<ApiResponse<LeadResult>> qualify(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.qualify(id)));
    }

    @PostMapping("/{id}/lose")
    public ResponseEntity<ApiResponse<LeadResult>> lose(@PathVariable Long id,
            @RequestBody(required = false) LeadActionRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.lose(id, body != null ? body.getReason() : null)));
    }

    // nhân viên tự nhận chăm sóc tiềm năng chưa có người phụ trách (pool chung)
    @PostMapping("/{id}/claim")
    public ResponseEntity<ApiResponse<LeadResult>> claim(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.claim(id, userId)));
    }

    @PreAuthorize("hasAuthority('lead.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResult>> create(@Valid @RequestBody CreateLeadCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LeadResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q, @RequestParam(required = false) String status) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        Long userId = (Long) req.getAttribute("userId");
        // admin/manager xem tất cả, nhân viên chỉ xem bản ghi mình phụ trách
        boolean privileged = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        Long ownerId = privileged ? null : userId;
        // nhân viên cũng thấy tiềm năng chưa có người phụ trách (pool chung) để bấm "Nhận chăm sóc"
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear)
                        .q(q).status(status).ownerId(ownerId).includeUnassigned(!privileged).build()))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    @PreAuthorize("hasAuthority('lead.edit')")
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

    @PreAuthorize("hasAuthority('lead.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    @PreAuthorize("hasAuthority('lead.delete')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PreAuthorize("hasAuthority('lead.delete')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PreAuthorize("hasAuthority('lead.import')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkLeadCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }

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
