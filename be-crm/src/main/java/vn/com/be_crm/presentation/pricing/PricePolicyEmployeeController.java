package vn.com.be_crm.presentation.pricing;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.ListPricePolicyEmployeeUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho nhân viên trong chính sách giá.
 */
@RestController
@RequestMapping("/api/price-policies/{policyId}/employees")
public class PricePolicyEmployeeController {
    private final CreatePricePolicyEmployeeUseCase createUC;
    private final DeletePricePolicyEmployeeUseCase deleteUC;
    private final ListPricePolicyEmployeeUseCase listUC;

    /** @param createUC tạo mới @param deleteUC xóa @param listUC danh sách */
    public PricePolicyEmployeeController(CreatePricePolicyEmployeeUseCase createUC,
                                          DeletePricePolicyEmployeeUseCase deleteUC, ListPricePolicyEmployeeUseCase listUC) {
        this.createUC = createUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Thêm nhân viên vào chính sách giá. @param policyId ID chính sách @param cmd body @return 201 */
    @PreAuthorize("hasAuthority('pricing.edit')")
    @PostMapping
    public ResponseEntity<ApiResponse<PricePolicyEmployeeResult>> create(@PathVariable Long policyId,
                                                                          @Valid @RequestBody CreatePricePolicyEmployeeCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreatePricePolicyEmployeeCommand.builder().pricePolicyId(policyId).userId(cmd.getUserId()).build())));
    }

    /** Lấy danh sách nhân viên. @param policyId ID chính sách @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PricePolicyEmployeeResult>>> list(@PathVariable Long policyId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(policyId)));
    }

    /** Xóa nhân viên. @param id ID @return 204 */
    @PreAuthorize("hasAuthority('pricing.edit')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long policyId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
