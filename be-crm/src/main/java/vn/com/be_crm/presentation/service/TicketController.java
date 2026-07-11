package vn.com.be_crm.presentation.service;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.service.command.*;
import vn.com.be_crm.application.service.dto.CreateTicketCommand;
import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.dto.UpdateTicketCommand;
import vn.com.be_crm.application.service.query.GetTicketUseCase;
import vn.com.be_crm.application.service.query.ListDeletedTicketsUseCase;
import vn.com.be_crm.application.service.query.ListTicketUseCase;
import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.service.request.AssignTicketRequest;
import vn.com.be_crm.presentation.service.request.CsatRequest;
import vn.com.be_crm.presentation.service.request.RejectTicketRequest;
import vn.com.be_crm.presentation.service.request.ResolveTicketRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.HandoverBulkRequest;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ phiếu hỗ trợ / dịch vụ sau bán.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final CreateTicketUseCase createUC;
    private final UpdateTicketUseCase updateUC;
    private final DeleteTicketUseCase deleteUC;
    private final GetTicketUseCase getUC;
    private final ListTicketUseCase listUC;
    private final ListDeletedTicketsUseCase listDeletedUC;
    private final RestoreTicketUseCase restoreUC;
    private final PurgeTicketUseCase purgeUC;
    private final HandoverBulkTicketUseCase handoverBulkUC;
    private final TicketWorkflowUseCase workflowUC;
    private final SubmitCsatUseCase csatUC;

    /** @param createUC tạo @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn
     *  @param handoverBulkUC bàn giao @param workflowUC luồng trạng thái @param csatUC đánh giá CSAT */
    public TicketController(CreateTicketUseCase createUC, UpdateTicketUseCase updateUC, DeleteTicketUseCase deleteUC,
                            GetTicketUseCase getUC, ListTicketUseCase listUC, ListDeletedTicketsUseCase listDeletedUC,
                            RestoreTicketUseCase restoreUC, PurgeTicketUseCase purgeUC,
                            HandoverBulkTicketUseCase handoverBulkUC, TicketWorkflowUseCase workflowUC,
                            SubmitCsatUseCase csatUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC; this.listDeletedUC = listDeletedUC;
        this.restoreUC = restoreUC; this.purgeUC = purgeUC; this.handoverBulkUC = handoverBulkUC;
        this.workflowUC = workflowUC; this.csatUC = csatUC;
    }

    /** Tạo mới phiếu. @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<TicketResult>> create(@Valid @RequestBody CreateTicketCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách phiếu. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TicketResult>>> list(
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

    /** Lấy danh sách phiếu trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Lấy phiếu theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật phiếu (KHÔNG nhận status). @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResult>> update(@PathVariable Long id,
                                                            @Valid @RequestBody UpdateTicketCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateTicketCommand.builder().id(id).type(cmd.getType()).subject(cmd.getSubject())
                        .description(cmd.getDescription()).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                        .invoiceId(cmd.getInvoiceId()).productId(cmd.getProductId()).channel(cmd.getChannel())
                        .priority(cmd.getPriority()).reason(cmd.getReason()).assignedUserId(cmd.getAssignedUserId())
                        .build())));
    }

    /** Xóa mềm phiếu. @param id ID @param req HTTP request @return 204 */
    @PreAuthorize("hasAuthority('ticket.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Khôi phục phiếu từ thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('ticket.delete')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn phiếu khỏi thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('ticket.delete')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Bàn giao hàng loạt phiếu sang người xử lý khác. @param body body @param req HTTP request @return 200 */
    @PostMapping("/handover-bulk")
    public ResponseEntity<ApiResponse<Void>> handoverBulk(@Valid @RequestBody HandoverBulkRequest body, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdminOrManager = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        handoverBulkUC.execute(HandoverBulkCommand.builder()
                .ids(body.getIds()).toUserId(body.getToUserId())
                .currentUserId(userId).adminOrManager(isAdminOrManager).reason(body.getReason()).build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ===== Hành động luồng trạng thái =====

    /** Giao phiếu cho nhân viên (new → assigned). @param id ID @param body người nhận @return 200 */
    @PreAuthorize("hasAuthority('ticket.process')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<TicketResult>> assign(@PathVariable Long id, @Valid @RequestBody AssignTicketRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.assign(id, body.getToUserId())));
    }

    /** Bắt đầu xử lý (assigned/reopened → in_progress). @param id ID @return 200 */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<TicketResult>> start(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.start(id)));
    }

    /** Giải quyết (support/complaint: in_progress → resolved). @param id ID @param body hình thức + ghi chú @return 200 */
    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<TicketResult>> resolve(@PathVariable Long id,
            @RequestBody(required = false) ResolveTicketRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.resolve(id,
                body != null ? body.getResolutionType() : null, body != null ? body.getNote() : null)));
    }

    /** Duyệt yêu cầu trả/đổi (in_progress → approved). @param id ID @param body ghi chú @return 200 */
    @PreAuthorize("hasAuthority('ticket.approve_return')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<TicketResult>> approve(@PathVariable Long id,
            @RequestBody(required = false) ResolveTicketRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.approve(id, body != null ? body.getNote() : null)));
    }

    /** Từ chối yêu cầu trả/đổi (in_progress → rejected). @param id ID @param body lý do @return 200 */
    @PreAuthorize("hasAuthority('ticket.approve_return')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<TicketResult>> reject(@PathVariable Long id,
            @RequestBody(required = false) RejectTicketRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.reject(id, body != null ? body.getReason() : null)));
    }

    /** Ghi nhận đã nhận hàng (approved → received). @param id ID @return 200 */
    @PostMapping("/{id}/receive")
    public ResponseEntity<ApiResponse<TicketResult>> receive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.receive(id)));
    }

    /** Ghi nhận đã kiểm hàng (received → inspected). @param id ID @return 200 */
    @PostMapping("/{id}/inspect")
    public ResponseEntity<ApiResponse<TicketResult>> inspect(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.inspect(id)));
    }

    /** Hoàn tất trả/đổi (inspected → resolved). @param id ID @param body hình thức + ghi chú @return 200 */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TicketResult>> complete(@PathVariable Long id,
            @RequestBody(required = false) ResolveTicketRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.complete(id,
                body != null ? body.getResolutionType() : null, body != null ? body.getNote() : null)));
    }

    /** Đóng phiếu (resolved/rejected → closed). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('ticket.process')")
    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<TicketResult>> close(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.close(id)));
    }

    /** Mở lại phiếu (resolved/closed → reopened). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('ticket.process')")
    @PostMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<TicketResult>> reopen(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.reopen(id)));
    }

    /** Ghi nhận đánh giá hài lòng CSAT. @param id ID @param body điểm + nhận xét @return 200 */
    @PostMapping("/{id}/csat")
    public ResponseEntity<ApiResponse<TicketResult>> csat(@PathVariable Long id, @Valid @RequestBody CsatRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(csatUC.execute(id, body.getScore(), body.getComment())));
    }
}
