package vn.com.be_crm.presentation.invoice;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.invoice.command.*;
import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.application.invoice.query.ListInvoiceRevenueRecordUseCase;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;

/**
 * REST controller cho bản ghi doanh thu đơn hàng.
 */
@RestController
@RequestMapping("/api/invoices/{invoiceId}/revenue-records")
public class InvoiceRevenueRecordController {
    private final CreateInvoiceRevenueRecordUseCase createUC;
    private final UpdateInvoiceRevenueRecordUseCase updateUC;
    private final DeleteInvoiceRevenueRecordUseCase deleteUC;
    private final ListInvoiceRevenueRecordUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public InvoiceRevenueRecordController(CreateInvoiceRevenueRecordUseCase createUC, UpdateInvoiceRevenueRecordUseCase updateUC,
                                         DeleteInvoiceRevenueRecordUseCase deleteUC, ListInvoiceRevenueRecordUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới bản ghi doanh thu. @param invoiceId ID @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceRevenueRecordResult>> create(@PathVariable Long invoiceId,
                                                                         @Valid @RequestBody CreateInvoiceRevenueRecordCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateInvoiceRevenueRecordCommand.builder().invoiceId(invoiceId).userId(cmd.getUserId())
                        .revenueAmount(cmd.getRevenueAmount()).percentage(cmd.getPercentage()).note(cmd.getNote()).build())));
    }

    /** Lấy danh sách bản ghi doanh thu. @param invoiceId ID @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceRevenueRecordResult>>> list(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(invoiceId)));
    }

    /** Cập nhật bản ghi doanh thu. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceRevenueRecordResult>> update(@PathVariable Long invoiceId, @PathVariable Long id,
                                                                         @Valid @RequestBody UpdateInvoiceRevenueRecordCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateInvoiceRevenueRecordCommand.builder().id(id).revenueAmount(cmd.getRevenueAmount())
                        .percentage(cmd.getPercentage()).note(cmd.getNote()).build())));
    }

    /** Xóa bản ghi doanh thu. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long invoiceId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
