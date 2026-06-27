package vn.com.be_crm.presentation.quotation;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.quotation.command.*;
import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.application.quotation.query.ListQuotationItemUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho dòng sản phẩm trong báo giá.
 */
@RestController
@RequestMapping("/api/quotations/{quotationId}/items")
public class QuotationItemController {
    private final CreateQuotationItemUseCase createUC;
    private final UpdateQuotationItemUseCase updateUC;
    private final DeleteQuotationItemUseCase deleteUC;
    private final ListQuotationItemUseCase listUC;
    private final SyncQuotationToOpportunityUseCase syncUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách @param syncUC đồng bộ về cơ hội */
    public QuotationItemController(CreateQuotationItemUseCase createUC, UpdateQuotationItemUseCase updateUC,
                                    DeleteQuotationItemUseCase deleteUC, ListQuotationItemUseCase listUC,
                                    SyncQuotationToOpportunityUseCase syncUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
        this.syncUC = syncUC;
    }

    /** Tạo mới dòng báo giá. @param quotationId ID báo giá @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<QuotationItemResult>> create(@PathVariable Long quotationId,
                                                                    @Valid @RequestBody CreateQuotationItemCommand cmd) {
        QuotationItemResult result = createUC.execute(
                CreateQuotationItemCommand.builder().quotationId(quotationId).productId(cmd.getProductId())
                        .unit(cmd.getUnit())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice()).discount(cmd.getDiscount())
                        .taxRate(cmd.getTaxRate()).amount(cmd.getAmount()).note(cmd.getNote()).build());
        syncUC.execute(quotationId);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    /** Lấy danh sách dòng báo giá. @param quotationId ID báo giá @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuotationItemResult>>> list(@PathVariable Long quotationId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(quotationId)));
    }

    /** Cập nhật dòng báo giá. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuotationItemResult>> update(@PathVariable Long quotationId,
                                                                    @PathVariable Long id,
                                                                    @Valid @RequestBody UpdateQuotationItemCommand cmd) {
        QuotationItemResult result = updateUC.execute(
                UpdateQuotationItemCommand.builder().id(id).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice()).discount(cmd.getDiscount())
                        .taxRate(cmd.getTaxRate()).amount(cmd.getAmount()).note(cmd.getNote()).build());
        syncUC.execute(quotationId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** Xóa dòng báo giá. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long quotationId, @PathVariable Long id) {
        deleteUC.execute(id);
        syncUC.execute(quotationId);
        return ResponseEntity.noContent().build();
    }
}
