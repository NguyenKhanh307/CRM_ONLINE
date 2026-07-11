package vn.com.be_crm.presentation.pricing;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý chính sách giá.
 */
@RestController
@RequestMapping("/api/price-policies")
public class PricePolicyController {
    private final CreatePricePolicyUseCase createUC;
    private final UpdatePricePolicyUseCase updateUC;
    private final DeletePricePolicyUseCase deleteUC;
    private final GetPricePolicyUseCase getUC;
    private final ListPricePolicyUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách */
    public PricePolicyController(CreatePricePolicyUseCase createUC, UpdatePricePolicyUseCase updateUC,
                                  DeletePricePolicyUseCase deleteUC, GetPricePolicyUseCase getUC, ListPricePolicyUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới chính sách giá. @param cmd JSON body @return 201 */
    @PreAuthorize("hasAuthority('pricing.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<PricePolicyResult>> create(@Valid @RequestBody CreatePricePolicyCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách chính sách giá. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PricePolicyResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy chính sách giá theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PricePolicyResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật chính sách giá. @param id ID @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('pricing.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PricePolicyResult>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdatePricePolicyCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdatePricePolicyCommand.builder().id(id).name(cmd.getName()).type(cmd.getType())
                        .priority(cmd.getPriority()).startDate(cmd.getStartDate()).endDate(cmd.getEndDate())
                        .status(cmd.getStatus()).build())));
    }

    /** Xóa chính sách giá. @param id ID @return 204 */
    @PreAuthorize("hasAuthority('pricing.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
