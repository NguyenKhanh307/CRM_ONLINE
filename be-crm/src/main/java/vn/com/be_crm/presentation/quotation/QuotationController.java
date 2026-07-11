package vn.com.be_crm.presentation.quotation;

import org.springframework.security.access.prepost.PreAuthorize;
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
import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.quotation.request.QuotationActionRequest;
import vn.com.be_crm.presentation.quotation.request.SendQuotationRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.HandoverBulkRequest;
import vn.com.be_crm.presentation.shared.PageResponse;
import vn.com.be_crm.domain.shared.exception.DomainException;

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
    private final ImportBulkQuotationUseCase importBulkUC;
    private final vn.com.be_crm.application.quotation.command.HandoverBulkQuotationUseCase handoverBulkUC;
    private final QuotationWorkflowUseCase workflowUC;
    private final CreateQuotationFromOpportunityUseCase fromOpportunityUC;
    private final RefreshQuotationItemsFromOpportunityUseCase refreshItemsUC;
    private final SetPrimaryQuotationUseCase setPrimaryUC;
    private final ConvertQuotationToOrderUseCase convertToOrderUC;
    private final GetQuotationEmailDraftUseCase emailDraftUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     *  @param handoverBulkUC bàn giao hàng loạt @param workflowUC luồng duyệt báo giá
     *  @param fromOpportunityUC clone từ cơ hội @param refreshItemsUC cập nhật lại dòng hàng từ cơ hội
     *  @param setPrimaryUC đặt báo giá đồng bộ @param convertToOrderUC chuyển thành đơn hàng */
    public QuotationController(CreateQuotationUseCase createUC, UpdateQuotationUseCase updateUC,
                                DeleteQuotationUseCase deleteUC, GetQuotationUseCase getUC, ListQuotationUseCase listUC,
                                ListDeletedQuotationsUseCase listDeletedUC, RestoreQuotationUseCase restoreUC, PurgeQuotationUseCase purgeUC,
                                ImportBulkQuotationUseCase importBulkUC,
                                vn.com.be_crm.application.quotation.command.HandoverBulkQuotationUseCase handoverBulkUC,
                                QuotationWorkflowUseCase workflowUC,
                                CreateQuotationFromOpportunityUseCase fromOpportunityUC,
                                RefreshQuotationItemsFromOpportunityUseCase refreshItemsUC,
                                SetPrimaryQuotationUseCase setPrimaryUC,
                                ConvertQuotationToOrderUseCase convertToOrderUC,
                                GetQuotationEmailDraftUseCase emailDraftUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC; this.handoverBulkUC = handoverBulkUC; this.workflowUC = workflowUC;
        this.fromOpportunityUC = fromOpportunityUC; this.refreshItemsUC = refreshItemsUC;
        this.setPrimaryUC = setPrimaryUC;
        this.convertToOrderUC = convertToOrderUC;
        this.emailDraftUC = emailDraftUC;
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
                        .opportunityId(cmd.getOpportunityId()).pricePolicyId(cmd.getPricePolicyId())
                        .ownerId(cmd.getOwnerId()).quoteDate(cmd.getQuoteDate()).validUntil(cmd.getValidUntil())
                        .subtotal(cmd.getSubtotal()).discount(cmd.getDiscount())
                        .tax(cmd.getTax()).total(cmd.getTotal()).note(cmd.getNote()).build())));
    }

    /** Clone báo giá từ cơ hội (sao chép sâu KH/LH/chính sách giá + dòng hàng). @param opportunityId ID cơ hội @return 201 */
    @PostMapping("/from-opportunity/{opportunityId}")
    public ResponseEntity<ApiResponse<QuotationResult>> fromOpportunity(@PathVariable Long opportunityId) {
        return ResponseEntity.status(201).body(ApiResponse.created(fromOpportunityUC.execute(opportunityId)));
    }

    /** Cập nhật lại dòng hàng báo giá theo cơ hội nguồn (xóa + clone lại OLI→QLI, giữ liên kết). @param id ID báo giá @return 200 */
    @PostMapping("/{id}/sync-items-from-opportunity")
    public ResponseEntity<ApiResponse<QuotationResult>> syncItemsFromOpportunity(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(refreshItemsUC.execute(id)));
    }

    /** Đặt báo giá làm báo giá đồng bộ (primary) của cơ hội. @param id ID @return 200 */
    @PostMapping("/{id}/set-primary")
    public ResponseEntity<ApiResponse<QuotationResult>> setPrimary(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(setPrimaryUC.execute(id)));
    }

    /** Khách chấp nhận báo giá (sent → accepted). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('quotation.approve')")
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<QuotationResult>> accept(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.accept(id)));
    }

    /** Chuyển báo giá thành đơn hàng (khóa báo giá + cơ hội Chốt Thắng). @param id ID @return 201 đơn hàng */
    @PostMapping("/{id}/convert-to-order")
    public ResponseEntity<ApiResponse<vn.com.be_crm.application.order.dto.OrderResult>> convertToOrder(@PathVariable Long id) {
        return ResponseEntity.status(201).body(ApiResponse.created(convertToOrderUC.execute(id)));
    }

    /** Xóa mềm báo giá. @param id ID @param req HTTP request @return 204 */
    @PreAuthorize("hasAnyAuthority('ADMIN','SALES_MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','SALES_MANAGER')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn báo giá khỏi thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAnyAuthority('ADMIN','SALES_MANAGER')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt báo giá từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('quotation.create')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkQuotationCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }

    /** Nhân viên gửi báo giá lên quản lý duyệt (draft → pending). @param id ID @param req HTTP request @return 200 */
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<QuotationResult>> submit(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.submit(id, userId)));
    }

    /** Quản lý duyệt báo giá (pending → approved). @param id ID @param body ý kiến @param req HTTP request @return 200 */
    @PreAuthorize("hasAuthority('quotation.approve')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<QuotationResult>> approve(@PathVariable Long id,
            @RequestBody(required = false) QuotationActionRequest body, HttpServletRequest req) {
        requireManager();
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.approve(id, userId, body != null ? body.getComment() : null)));
    }

    /** Quản lý từ chối báo giá (pending → draft). @param id ID @param body lý do @param req HTTP request @return 200 */
    @PreAuthorize("hasAuthority('quotation.approve')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<QuotationResult>> reject(@PathVariable Long id,
            @RequestBody(required = false) QuotationActionRequest body, HttpServletRequest req) {
        requireManager();
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.reject(id, userId, body != null ? body.getComment() : null)));
    }

    /** Lấy nội dung email báo giá mặc định để FE hiển thị trước khi gửi. @param id ID @return 200 */
    @GetMapping("/{id}/email-draft")
    public ResponseEntity<ApiResponse<vn.com.be_crm.application.quotation.dto.QuotationEmailDraft>> emailDraft(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(emailDraftUC.execute(id)));
    }

    /** Nhân viên gửi email báo giá cho khách (approved → sent). @param id ID @param body tiêu đề/nội dung tùy biến @return 200 */
    @PreAuthorize("hasAuthority('quotation.approve')")
    @PostMapping("/{id}/send")
    public ResponseEntity<ApiResponse<QuotationResult>> send(@PathVariable Long id,
            @RequestBody(required = false) SendQuotationRequest body) {
        String subject = body != null ? body.getSubject() : null;
        String content = body != null ? body.getBody() : null;
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.send(id, subject, content)));
    }

    /** Chỉ ADMIN/SALES_MANAGER mới được duyệt/từ chối báo giá. */
    private void requireManager() {
        if (!SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication())) {
            throw new DomainException("Chỉ quản lý mới được duyệt hoặc từ chối báo giá");
        }
    }

    /** Bàn giao hàng loạt báo giá sang người dùng khác. @param body body @param req HTTP request @return 200 */
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
