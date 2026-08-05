package vn.com.be_crm.presentation.invoice;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.invoice.command.*;
import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.application.invoice.query.ListInvoiceItemUseCase;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;

// REST controller cho dòng sản phẩm trong hóa đơn
@RestController
@RequestMapping("/api/invoices/{invoiceId}/items")
public class InvoiceItemController {
    private final CreateInvoiceItemUseCase createUC;
    private final UpdateInvoiceItemUseCase updateUC;
    private final DeleteInvoiceItemUseCase deleteUC;
    private final ListInvoiceItemUseCase listUC;

    public InvoiceItemController(CreateInvoiceItemUseCase createUC, UpdateInvoiceItemUseCase updateUC,
                                DeleteInvoiceItemUseCase deleteUC, ListInvoiceItemUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    // tạo mới dòng hóa đơn
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceItemResult>> create(@PathVariable Long invoiceId,
                                                                @Valid @RequestBody CreateInvoiceItemCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateInvoiceItemCommand.builder().invoiceId(invoiceId).productId(cmd.getProductId())
                        .unit(cmd.getUnit())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice())
                        .discount(cmd.getDiscount()).taxRate(cmd.getTaxRate())
                        .note(cmd.getNote()).build())));
    }

    // lấy danh sách dòng hóa đơn
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceItemResult>>> list(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(invoiceId)));
    }

    // cập nhật dòng hóa đơn
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceItemResult>> update(@PathVariable Long invoiceId, @PathVariable Long id,
                                                                @Valid @RequestBody UpdateInvoiceItemCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateInvoiceItemCommand.builder().id(id).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice())
                        .discount(cmd.getDiscount()).taxRate(cmd.getTaxRate())
                        .note(cmd.getNote()).build())));
    }

    // xóa dòng hóa đơn
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long invoiceId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
