package vn.com.be_crm.presentation.order;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.application.order.query.*;
import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.dto.handover.HandoverBulkCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.dto.handover.HandoverBulkRequest;
import vn.com.be_crm.core.page.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý đơn hàng.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final CreateOrderUseCase createUC;
    private final UpdateOrderUseCase updateUC;
    private final DeleteOrderUseCase deleteUC;
    private final GetOrderUseCase getUC;
    private final ListOrderUseCase listUC;
    private final ListDeletedOrdersUseCase listDeletedUC;
    private final RestoreOrderUseCase restoreUC;
    private final PurgeOrderUseCase purgeUC;
    private final ImportBulkOrderUseCase importBulkUC;
    private final HandoverBulkOrderUseCase handoverBulkUC;
    private final OrderWorkflowUseCase workflowUC;
    private final MarkOrderConvertedUseCase markConvertedUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     *  @param handoverBulkUC bàn giao hàng loạt @param workflowUC luồng trạng thái
     *  @param markConvertedUC đánh dấu đã xuất hóa đơn (khóa đơn hàng + hoàn tất) */
    public OrderController(CreateOrderUseCase createUC, UpdateOrderUseCase updateUC, DeleteOrderUseCase deleteUC,
                           GetOrderUseCase getUC, ListOrderUseCase listUC,
                           ListDeletedOrdersUseCase listDeletedUC, RestoreOrderUseCase restoreUC, PurgeOrderUseCase purgeUC,
                           ImportBulkOrderUseCase importBulkUC,
                           HandoverBulkOrderUseCase handoverBulkUC,
                           OrderWorkflowUseCase workflowUC,
                           MarkOrderConvertedUseCase markConvertedUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC; this.handoverBulkUC = handoverBulkUC; this.workflowUC = workflowUC;
        this.markConvertedUC = markConvertedUC;
    }

    /** Tạo mới đơn hàng. @param cmd JSON body @return 201 */
    @PreAuthorize("hasAuthority('order.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResult>> create(@Valid @RequestBody CreateOrderCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách đơn hàng. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResult>>> list(
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

    /** Lấy đơn hàng theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật đơn hàng. @param id ID @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('order.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateOrderCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOrderCommand.builder().id(id)
                        .quotationId(cmd.getQuotationId()).ownerId(cmd.getOwnerId())
                        .orderDate(cmd.getOrderDate()).deliveryDate(cmd.getDeliveryDate())
                        .note(cmd.getNote()).build())));
    }

    /** Xác nhận đơn hàng (draft → confirmed). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('order.process')")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderResult>> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.confirm(id)));
    }

    /** Chuyển đơn hàng sang đang xử lý (confirmed → processing). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('order.process')")
    @PostMapping("/{id}/process")
    public ResponseEntity<ApiResponse<OrderResult>> process(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.process(id)));
    }

    /** Hoàn tất đơn hàng (→ completed). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('order.process')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<OrderResult>> complete(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.complete(id)));
    }

    /** Hủy đơn hàng (→ cancelled). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('order.process')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResult>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.cancel(id)));
    }

    /** Mở lại khi lỡ bấm nhầm (completed → processing, cancelled → draft). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('order.process')")
    @PostMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<OrderResult>> reopen(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.reopen(id)));
    }

    /** Đánh dấu đơn hàng đã xuất hóa đơn (Hóa đơn đã được tạo riêng qua trang thêm mới
     * ?fromOrder=; ở đây chỉ khóa đơn hàng + chuyển sang Hoàn tất). @param id ID đơn hàng @return 200 */
    @PreAuthorize("hasAuthority('order.create_invoice')")
    @PostMapping("/{id}/mark-converted")
    public ResponseEntity<ApiResponse<OrderResult>> markConverted(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(markConvertedUC.execute(id)));
    }

    /** Xóa mềm đơn hàng. @param id ID @param req HTTP request @return 204 */
    @PreAuthorize("hasAuthority('order.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Lấy danh sách đơn hàng trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Khôi phục đơn hàng từ thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('order.delete')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn đơn hàng khỏi thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('order.delete')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt đơn hàng từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('order.import')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkOrderCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }

    /** Bàn giao hàng loạt đơn hàng sang người dùng khác. @param body body @param req HTTP request @return 200 */
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
