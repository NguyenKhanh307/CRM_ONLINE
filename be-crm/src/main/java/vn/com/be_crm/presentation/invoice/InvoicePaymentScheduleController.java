package vn.com.be_crm.presentation.invoice;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.invoice.command.*;
import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.application.invoice.query.ListInvoicePaymentScheduleUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho đợt thanh toán đơn hàng.
 */
@RestController
@RequestMapping("/api/invoices/{invoiceId}/payment-schedules")
public class InvoicePaymentScheduleController {
    private final CreateInvoicePaymentScheduleUseCase createUC;
    private final UpdateInvoicePaymentScheduleUseCase updateUC;
    private final DeleteInvoicePaymentScheduleUseCase deleteUC;
    private final ListInvoicePaymentScheduleUseCase listUC;
    private final InvoiceWorkflowUseCase workflowUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách @param workflowUC suy ra trạng thái thanh toán */
    public InvoicePaymentScheduleController(CreateInvoicePaymentScheduleUseCase createUC, UpdateInvoicePaymentScheduleUseCase updateUC,
                                           DeleteInvoicePaymentScheduleUseCase deleteUC, ListInvoicePaymentScheduleUseCase listUC,
                                           InvoiceWorkflowUseCase workflowUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
        this.workflowUC = workflowUC;
    }

    /** Tạo mới đợt thanh toán. @param invoiceId ID @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<InvoicePaymentScheduleResult>> create(@PathVariable Long invoiceId,
                                                                           @Valid @RequestBody CreateInvoicePaymentScheduleCommand cmd) {
        InvoicePaymentScheduleResult result = createUC.execute(
                CreateInvoicePaymentScheduleCommand.builder().invoiceId(invoiceId).installmentNo(cmd.getInstallmentNo())
                        .dueDate(cmd.getDueDate()).amount(cmd.getAmount()).paidAmount(cmd.getPaidAmount())
                        .status(cmd.getStatus()).paidAt(cmd.getPaidAt()).note(cmd.getNote()).build());
        workflowUC.recalcPaymentStatus(invoiceId);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    /** Lấy danh sách đợt thanh toán. @param invoiceId ID @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoicePaymentScheduleResult>>> list(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(invoiceId)));
    }

    /** Cập nhật đợt thanh toán. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoicePaymentScheduleResult>> update(@PathVariable Long invoiceId, @PathVariable Long id,
                                                                           @Valid @RequestBody UpdateInvoicePaymentScheduleCommand cmd) {
        InvoicePaymentScheduleResult result = updateUC.execute(
                UpdateInvoicePaymentScheduleCommand.builder().id(id).dueDate(cmd.getDueDate())
                        .amount(cmd.getAmount()).paidAmount(cmd.getPaidAmount()).status(cmd.getStatus())
                        .paidAt(cmd.getPaidAt()).note(cmd.getNote()).build());
        workflowUC.recalcPaymentStatus(invoiceId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** Xóa đợt thanh toán. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long invoiceId, @PathVariable Long id) {
        deleteUC.execute(id);
        workflowUC.recalcPaymentStatus(invoiceId);
        return ResponseEntity.noContent().build();
    }
}
