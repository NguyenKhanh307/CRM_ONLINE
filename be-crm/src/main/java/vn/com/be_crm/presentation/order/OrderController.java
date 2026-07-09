package vn.com.be_crm.presentation.order;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.application.order.query.*;
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
    private final CreateInvoiceFromOrderUseCase createInvoiceUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     *  @param handoverBulkUC bàn giao hàng loạt @param workflowUC luồng trạng thái @param createInvoiceUC xuất hóa đơn từ đơn hàng */
    public OrderController(CreateOrderUseCase createUC, UpdateOrderUseCase updateUC, DeleteOrderUseCase deleteUC,
                           GetOrderUseCase getUC, ListOrderUseCase listUC,
                           ListDeletedOrdersUseCase listDeletedUC, RestoreOrderUseCase restoreUC, PurgeOrderUseCase purgeUC,
                           ImportBulkOrderUseCase importBulkUC,
                           HandoverBulkOrderUseCase handoverBulkUC,
                           OrderWorkflowUseCase workflowUC,
                           CreateInvoiceFromOrderUseCase createInvoiceUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC; this.handoverBulkUC = handoverBulkUC; this.workflowUC = workflowUC;
        this.createInvoiceUC = createInvoiceUC;
    }

    /** Tạo mới đơn hàng. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResult>> create(@Valid @RequestBody CreateOrderCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách đơn hàng. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).build()))));
    }

    /** Lấy đơn hàng theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật đơn hàng. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateOrderCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOrderCommand.builder().id(id).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                        .quotationId(cmd.getQuotationId()).opportunityId(cmd.getOpportunityId())
                        .campaignId(cmd.getCampaignId()).ownerId(cmd.getOwnerId())
                        .orderDate(cmd.getOrderDate()).deliveryDate(cmd.getDeliveryDate())
                        .currency(cmd.getCurrency()).exchangeRate(cmd.getExchangeRate())
                        .billingAddress(cmd.getBillingAddress()).taxCode(cmd.getTaxCode())
                        .subtotal(cmd.getSubtotal()).discount(cmd.getDiscount()).tax(cmd.getTax())
                        .total(cmd.getTotal()).note(cmd.getNote()).build())));
    }

    /** Xác nhận đơn hàng (draft → confirmed). @param id ID @return 200 */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderResult>> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.confirm(id)));
    }

    /** Chuyển đơn hàng sang đang xử lý (confirmed → processing). @param id ID @return 200 */
    @PostMapping("/{id}/process")
    public ResponseEntity<ApiResponse<OrderResult>> process(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.process(id)));
    }

    /** Hoàn tất đơn hàng (→ completed). @param id ID @return 200 */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<OrderResult>> complete(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.complete(id)));
    }

    /** Hủy đơn hàng (→ cancelled). @param id ID @return 200 */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResult>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.cancel(id)));
    }

    /** Xuất hóa đơn từ đơn hàng (1-1, khóa đơn hàng + đơn hàng completed). @param id ID đơn hàng @return 201 hóa đơn */
    @PostMapping("/{id}/create-invoice")
    public ResponseEntity<ApiResponse<InvoiceResult>> createInvoice(@PathVariable Long id) {
        return ResponseEntity.status(201).body(ApiResponse.created(createInvoiceUC.execute(id)));
    }

    /** Xóa mềm đơn hàng. @param id ID @param req HTTP request @return 204 */
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
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn đơn hàng khỏi thùng rác. @param id ID @return 200 */
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt đơn hàng từ file. @param cmd body @return 200 */
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
