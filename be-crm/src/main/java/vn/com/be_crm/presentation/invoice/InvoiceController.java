package vn.com.be_crm.presentation.invoice;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.invoice.command.*;
import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.application.invoice.query.*;
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
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final CreateInvoiceUseCase createUC;
    private final UpdateInvoiceUseCase updateUC;
    private final DeleteInvoiceUseCase deleteUC;
    private final GetInvoiceUseCase getUC;
    private final ListInvoiceUseCase listUC;
    private final ListDeletedInvoicesUseCase listDeletedUC;
    private final RestoreInvoiceUseCase restoreUC;
    private final PurgeInvoiceUseCase purgeUC;
    private final ImportBulkInvoiceUseCase importBulkUC;
    private final vn.com.be_crm.application.invoice.command.HandoverBulkInvoiceUseCase handoverBulkUC;
    private final InvoiceWorkflowUseCase workflowUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách
     *  @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     *  @param handoverBulkUC bàn giao hàng loạt @param workflowUC luồng trạng thái */
    public InvoiceController(CreateInvoiceUseCase createUC, UpdateInvoiceUseCase updateUC, DeleteInvoiceUseCase deleteUC,
                            GetInvoiceUseCase getUC, ListInvoiceUseCase listUC,
                            ListDeletedInvoicesUseCase listDeletedUC, RestoreInvoiceUseCase restoreUC, PurgeInvoiceUseCase purgeUC,
                            ImportBulkInvoiceUseCase importBulkUC,
                            vn.com.be_crm.application.invoice.command.HandoverBulkInvoiceUseCase handoverBulkUC,
                            InvoiceWorkflowUseCase workflowUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC; this.handoverBulkUC = handoverBulkUC; this.workflowUC = workflowUC;
    }

    /** Tạo mới đơn hàng. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResult>> create(@Valid @RequestBody CreateInvoiceCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách đơn hàng. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<InvoiceResult>>> list(
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
    public ResponseEntity<ApiResponse<InvoiceResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật đơn hàng. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateInvoiceCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateInvoiceCommand.builder().id(id).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                        .quotationId(cmd.getQuotationId()).opportunityId(cmd.getOpportunityId())
                        .campaignId(cmd.getCampaignId())
                        .ownerId(cmd.getOwnerId())
                        .invoiceDate(cmd.getInvoiceDate()).dueDate(cmd.getDueDate())
                        .currency(cmd.getCurrency()).exchangeRate(cmd.getExchangeRate())
                        .billingAddress(cmd.getBillingAddress()).taxCode(cmd.getTaxCode())
                        .subtotal(cmd.getSubtotal()).discount(cmd.getDiscount()).tax(cmd.getTax())
                        .total(cmd.getTotal()).note(cmd.getNote()).build())));
    }

    /** Phát hành hóa đơn (draft → sent, khóa dữ liệu). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('invoice.approve')")
    @PostMapping("/{id}/issue")
    public ResponseEntity<ApiResponse<InvoiceResult>> issue(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.issue(id)));
    }

    /** Hủy hóa đơn (→ cancelled). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('invoice.approve')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<InvoiceResult>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.cancel(id)));
    }

    /** Xóa mềm đơn hàng. @param id ID @param req HTTP request @return 204 */
    @PreAuthorize("hasAnyAuthority('ADMIN','SALES_MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','SALES_MANAGER')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn đơn hàng khỏi thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAnyAuthority('ADMIN','SALES_MANAGER')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt đơn hàng từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('invoice.create')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkInvoiceCommand cmd) {
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
