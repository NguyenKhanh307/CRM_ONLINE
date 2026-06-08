package vn.com.be_crm.presentation.pricing;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.ListPricePolicyCustomerUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho khách hàng trong chính sách giá.
 */
@RestController
@RequestMapping("/api/price-policies/{policyId}/customers")
public class PricePolicyCustomerController {
    private final CreatePricePolicyCustomerUseCase createUC;
    private final DeletePricePolicyCustomerUseCase deleteUC;
    private final ListPricePolicyCustomerUseCase listUC;

    /** @param createUC tạo mới @param deleteUC xóa @param listUC danh sách */
    public PricePolicyCustomerController(CreatePricePolicyCustomerUseCase createUC,
                                          DeletePricePolicyCustomerUseCase deleteUC, ListPricePolicyCustomerUseCase listUC) {
        this.createUC = createUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Thêm khách hàng vào chính sách giá. @param policyId ID chính sách @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<PricePolicyCustomerResult>> create(@PathVariable Long policyId,
                                                                          @Valid @RequestBody CreatePricePolicyCustomerCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreatePricePolicyCustomerCommand.builder().pricePolicyId(policyId).customerId(cmd.getCustomerId()).build())));
    }

    /** Lấy danh sách khách hàng. @param policyId ID chính sách @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PricePolicyCustomerResult>>> list(@PathVariable Long policyId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(policyId)));
    }

    /** Xóa khách hàng. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long policyId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
