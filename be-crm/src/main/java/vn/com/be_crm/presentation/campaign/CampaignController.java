package vn.com.be_crm.presentation.campaign;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.campaign.command.*;
import vn.com.be_crm.application.campaign.dto.*;
import vn.com.be_crm.application.campaign.query.*;
import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.HandoverBulkRequest;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý chiến dịch marketing.
 */
@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
    private final CreateCampaignUseCase createUC;
    private final UpdateCampaignUseCase updateUC;
    private final DeleteCampaignUseCase deleteUC;
    private final GetCampaignUseCase getUC;
    private final ListCampaignUseCase listUC;
    private final ListDeletedCampaignsUseCase listDeletedUC;
    private final RestoreCampaignUseCase restoreUC;
    private final PurgeCampaignUseCase purgeUC;
    private final ImportBulkCampaignUseCase importBulkUC;
    private final HandoverBulkCampaignUseCase handoverBulkUC;
    private final CampaignWorkflowUseCase workflowUC;
    private final SendCampaignEmailUseCase sendEmailUC;
    private final GetCampaignStatsUseCase statsUC;

    /** @param createUC tạo @param updateUC sửa @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập
     *  @param handoverBulkUC bàn giao @param workflowUC luồng trạng thái @param sendEmailUC gửi email @param statsUC thống kê ROI */
    public CampaignController(CreateCampaignUseCase createUC, UpdateCampaignUseCase updateUC, DeleteCampaignUseCase deleteUC,
                              GetCampaignUseCase getUC, ListCampaignUseCase listUC,
                              ListDeletedCampaignsUseCase listDeletedUC, RestoreCampaignUseCase restoreUC, PurgeCampaignUseCase purgeUC,
                              ImportBulkCampaignUseCase importBulkUC, HandoverBulkCampaignUseCase handoverBulkUC,
                              CampaignWorkflowUseCase workflowUC, SendCampaignEmailUseCase sendEmailUC, GetCampaignStatsUseCase statsUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC; this.handoverBulkUC = handoverBulkUC; this.workflowUC = workflowUC;
        this.sendEmailUC = sendEmailUC; this.statsUC = statsUC;
    }

    /** Tạo mới chiến dịch. @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<CampaignResult>> create(@Valid @RequestBody CreateCampaignCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách chiến dịch. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CampaignResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).build()))));
    }

    /** Lấy chiến dịch theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Lấy thống kê hiệu quả (ROI) của chiến dịch. @param id ID @return 200 */
    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<CampaignStatsResult>> stats(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(statsUC.execute(id)));
    }

    /** Cập nhật chiến dịch. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateCampaignCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateCampaignCommand.builder().id(id).name(cmd.getName()).type(cmd.getType())
                        .channel(cmd.getChannel()).startDate(cmd.getStartDate()).endDate(cmd.getEndDate())
                        .budget(cmd.getBudget()).actualCost(cmd.getActualCost()).targetSize(cmd.getTargetSize())
                        .expectedRevenue(cmd.getExpectedRevenue()).ownerId(cmd.getOwnerId())
                        .description(cmd.getDescription()).build())));
    }

    /** Lên lịch chiến dịch (→ scheduled). @param id ID @return 200 */
    @PostMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<CampaignResult>> schedule(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.schedule(id)));
    }

    /** Bắt đầu chạy chiến dịch (→ running). @param id ID @return 200 */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<CampaignResult>> start(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.start(id)));
    }

    /** Tạm dừng chiến dịch (→ paused). @param id ID @return 200 */
    @PostMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<CampaignResult>> pause(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.pause(id)));
    }

    /** Hoàn tất chiến dịch (→ completed). @param id ID @return 200 */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<CampaignResult>> complete(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.complete(id)));
    }

    /** Hủy chiến dịch (→ cancelled). @param id ID @return 200 */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<CampaignResult>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.cancel(id)));
    }

    /** Gửi email hàng loạt cho thành viên chiến dịch. @param id ID @param cmd body @return 200 số email đã gửi */
    @PostMapping("/{id}/send-email")
    public ResponseEntity<ApiResponse<Integer>> sendEmail(@PathVariable Long id, @Valid @RequestBody SendCampaignEmailCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(sendEmailUC.execute(
                SendCampaignEmailCommand.builder().campaignId(id).subject(cmd.getSubject()).body(cmd.getBody()).build())));
    }

    /** Xóa mềm chiến dịch. @param id ID @param req HTTP request @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Lấy danh sách chiến dịch trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Khôi phục chiến dịch từ thùng rác. @param id ID @return 200 */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn chiến dịch khỏi thùng rác. @param id ID @return 200 */
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt chiến dịch từ file. @param cmd body @return 200 */
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkCampaignCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }

    /** Bàn giao hàng loạt chiến dịch sang người dùng khác. @param body body @param req HTTP request @return 200 */
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
