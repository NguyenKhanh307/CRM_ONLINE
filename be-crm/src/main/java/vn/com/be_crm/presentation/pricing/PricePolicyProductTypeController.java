package vn.com.be_crm.presentation.pricing;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.ListPricePolicyProductTypeUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho loại sản phẩm trong chính sách giá.
 */
@RestController
@RequestMapping("/api/price-policies/{policyId}/product-types")
public class PricePolicyProductTypeController {
    private final CreatePricePolicyProductTypeUseCase createUC;
    private final DeletePricePolicyProductTypeUseCase deleteUC;
    private final ListPricePolicyProductTypeUseCase listUC;

    /** @param createUC tạo mới @param deleteUC xóa @param listUC danh sách */
    public PricePolicyProductTypeController(CreatePricePolicyProductTypeUseCase createUC,
                                             DeletePricePolicyProductTypeUseCase deleteUC,
                                             ListPricePolicyProductTypeUseCase listUC) {
        this.createUC = createUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Thêm loại sản phẩm. @param policyId ID chính sách @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<PricePolicyProductTypeResult>> create(@PathVariable Long policyId,
                                                                             @Valid @RequestBody CreatePricePolicyProductTypeCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreatePricePolicyProductTypeCommand.builder().pricePolicyId(policyId).productTypeId(cmd.getProductTypeId()).build())));
    }

    /** Lấy danh sách loại sản phẩm. @param policyId ID chính sách @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PricePolicyProductTypeResult>>> list(@PathVariable Long policyId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(policyId)));
    }

    /** Xóa loại sản phẩm. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long policyId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
