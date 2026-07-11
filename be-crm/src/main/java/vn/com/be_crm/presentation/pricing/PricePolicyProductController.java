package vn.com.be_crm.presentation.pricing;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.ListPricePolicyProductUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho sản phẩm trong chính sách giá.
 */
@RestController
@RequestMapping("/api/price-policies/{policyId}/products")
public class PricePolicyProductController {
    private final CreatePricePolicyProductUseCase createUC;
    private final UpdatePricePolicyProductUseCase updateUC;
    private final DeletePricePolicyProductUseCase deleteUC;
    private final ListPricePolicyProductUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public PricePolicyProductController(CreatePricePolicyProductUseCase createUC, UpdatePricePolicyProductUseCase updateUC,
                                         DeletePricePolicyProductUseCase deleteUC, ListPricePolicyProductUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới sản phẩm trong chính sách giá. @param policyId ID chính sách @param cmd body @return 201 */
    @PreAuthorize("hasAuthority('pricing.edit')")
    @PostMapping
    public ResponseEntity<ApiResponse<PricePolicyProductResult>> create(@PathVariable Long policyId,
                                                                         @Valid @RequestBody CreatePricePolicyProductCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreatePricePolicyProductCommand.builder().pricePolicyId(policyId).productId(cmd.getProductId())
                        .price(cmd.getPrice()).discountType(cmd.getDiscountType()).discountValue(cmd.getDiscountValue())
                        .minQty(cmd.getMinQty()).build())));
    }

    /** Lấy danh sách sản phẩm. @param policyId ID chính sách @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PricePolicyProductResult>>> list(@PathVariable Long policyId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(policyId)));
    }

    /** Cập nhật sản phẩm. @param id ID @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('pricing.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PricePolicyProductResult>> update(@PathVariable Long policyId, @PathVariable Long id,
                                                                         @Valid @RequestBody UpdatePricePolicyProductCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdatePricePolicyProductCommand.builder().id(id).price(cmd.getPrice()).discountType(cmd.getDiscountType())
                        .discountValue(cmd.getDiscountValue()).minQty(cmd.getMinQty()).build())));
    }

    /** Xóa sản phẩm. @param id ID @return 204 */
    @PreAuthorize("hasAuthority('pricing.edit')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long policyId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
