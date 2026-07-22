package vn.com.be_crm.presentation.opportunity;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.opportunity.command.*;
import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.application.opportunity.query.*;
import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.opportunity.request.ChangeStageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.HandoverBulkRequest;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý cơ hội bán hàng.
 */
@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {
    private final CreateOpportunityUseCase createUC;
    private final UpdateOpportunityUseCase updateUC;
    private final DeleteOpportunityUseCase deleteUC;
    private final GetOpportunityUseCase getUC;
    private final ListOpportunityUseCase listUC;
    private final ListDeletedOpportunitiesUseCase listDeletedUC;
    private final RestoreOpportunityUseCase restoreUC;
    private final PurgeOpportunityUseCase purgeUC;
    private final ImportBulkOpportunityUseCase importBulkUC;
    private final HandoverBulkOpportunityUseCase handoverBulkUC;
    private final ChangeOpportunityStageUseCase changeStageUC;
    private final GetOpportunityBoardUseCase boardUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     *  @param handoverBulkUC bàn giao hàng loạt @param changeStageUC đổi giai đoạn (Kanban) @param boardUC nạp bảng Kanban */
    public OpportunityController(CreateOpportunityUseCase createUC, UpdateOpportunityUseCase updateUC,
                                  DeleteOpportunityUseCase deleteUC, GetOpportunityUseCase getUC,
                                  ListOpportunityUseCase listUC, ListDeletedOpportunitiesUseCase listDeletedUC,
                                  RestoreOpportunityUseCase restoreUC, PurgeOpportunityUseCase purgeUC,
                                  ImportBulkOpportunityUseCase importBulkUC, HandoverBulkOpportunityUseCase handoverBulkUC,
                                  ChangeOpportunityStageUseCase changeStageUC, GetOpportunityBoardUseCase boardUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC; this.handoverBulkUC = handoverBulkUC;
        this.changeStageUC = changeStageUC; this.boardUC = boardUC;
    }

    /**
     * Nạp bảng Kanban: cột = giai đoạn pipeline, kèm số cơ hội, tổng tiền và tối đa 50 thẻ mỗi cột.
     * Lọc theo owner giống list (nhân viên chỉ thấy cơ hội mình phụ trách).
     *
     * @param req HTTP request (userId + dataAccessFromYear từ JWT)
     * @param q   từ khóa tìm theo mã/tên cơ hội
     * @return 200 kèm danh sách cột
     */
    @GetMapping("/board")
    public ResponseEntity<ApiResponse<java.util.List<BoardColumnResult>>> board(
            HttpServletRequest req, @RequestParam(required = false) String q) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        Long userId = (Long) req.getAttribute("userId");
        boolean privileged = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(boardUC.execute(privileged ? null : userId, fromYear, q)));
    }

    /**
     * Đổi giai đoạn pipeline của cơ hội (kéo-thả trên bảng Kanban).
     * Trạng thái open/won/lost tự suy ra từ giai đoạn — client không gửi status.
     *
     * @param id   ID cơ hội
     * @param body giai đoạn đích + lý do thắng/thua (tùy chọn)
     * @return 200 kèm cơ hội sau cập nhật
     */
    @PostMapping("/{id}/stage")
    public ResponseEntity<ApiResponse<OpportunityResult>> changeStage(@PathVariable Long id,
                                                                       @Valid @RequestBody ChangeStageRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(changeStageUC.execute(ChangeOpportunityStageCommand.builder()
                .id(id).stageId(body.getStageId()).winLossReason(body.getWinLossReason()).build())));
    }

    /** Tạo mới cơ hội. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OpportunityResult>> create(@Valid @RequestBody CreateOpportunityCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách cơ hội. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OpportunityResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q, @RequestParam(required = false) String status) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        // Record-level visibility: admin/manager xem tat ca, nhan vien chi xem ban ghi minh phu trach
        Long userId = (Long) req.getAttribute("userId");
        boolean privileged = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        Long ownerId = privileged ? null : userId;
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).q(q).status(status).ownerId(ownerId).build()))));
    }

    /** Lấy cơ hội theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật cơ hội. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityResult>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdateOpportunityCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOpportunityCommand.builder().id(id).name(cmd.getName()).opportunityType(cmd.getOpportunityType())
                        .customerId(cmd.getCustomerId())
                        .contactId(cmd.getContactId()).ownerId(cmd.getOwnerId()).stageId(cmd.getStageId())
                        .pricePolicyId(cmd.getPricePolicyId())
                        .amount(cmd.getAmount()).expectedRevenue(cmd.getExpectedRevenue())
                        .expectedCloseDate(cmd.getExpectedCloseDate())
                        .source(cmd.getSource()).campaignId(cmd.getCampaignId()).winLossReason(cmd.getWinLossReason()).description(cmd.getDescription())
                        .status(cmd.getStatus()).build())));
    }

    /** Xóa mềm cơ hội. @param id ID @param req HTTP request @return 204 */
    @PreAuthorize("hasAuthority('opportunity.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Lấy danh sách cơ hội trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Khôi phục cơ hội từ thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('opportunity.delete')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn cơ hội khỏi thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('opportunity.delete')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt cơ hội từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('opportunity.create')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkOpportunityCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }

    /** Bàn giao hàng loạt cơ hội sang người dùng khác. @param body body @param req HTTP request @return 200 */
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
