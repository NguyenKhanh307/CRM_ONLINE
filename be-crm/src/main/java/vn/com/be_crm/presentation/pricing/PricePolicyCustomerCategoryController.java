package vn.com.be_crm.presentation.pricing;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.ListPricePolicyCustomerCategoryUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho danh mục khách hàng trong chính sách giá.
 */
@RestController
@RequestMapping("/api/price-policies/{policyId}/customer-categories")
public class PricePolicyCustomerCategoryController {
    private final CreatePricePolicyCustomerCategoryUseCase createUC;
    private final DeletePricePolicyCustomerCategoryUseCase deleteUC;
    private final ListPricePolicyCustomerCategoryUseCase listUC;

    /** @param createUC tạo mới @param deleteUC xóa @param listUC danh sách */
    public PricePolicyCustomerCategoryController(CreatePricePolicyCustomerCategoryUseCase createUC,
                                                  DeletePricePolicyCustomerCategoryUseCase deleteUC,
                                                  ListPricePolicyCustomerCategoryUseCase listUC) {
        this.createUC = createUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Thêm danh mục khách hàng. @param policyId ID chính sách @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<PricePolicyCustomerCategoryResult>> create(@PathVariable Long policyId,
                                                                                  @Valid @RequestBody CreatePricePolicyCustomerCategoryCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreatePricePolicyCustomerCategoryCommand.builder().pricePolicyId(policyId).categoryId(cmd.getCategoryId()).build())));
    }

    /** Lấy danh sách danh mục khách hàng. @param policyId ID chính sách @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PricePolicyCustomerCategoryResult>>> list(@PathVariable Long policyId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(policyId)));
    }

    /** Xóa danh mục khách hàng. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long policyId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
