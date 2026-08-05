package vn.com.be_crm.presentation.pricing;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.*;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.page.PageResponse;

import java.util.List;

// REST controller cho nghiệp vụ quản lý chính sách giá
@RestController
@RequestMapping("/api/price-policies")
public class PricePolicyController {
    private final CreatePricePolicyUseCase createUC;
    private final UpdatePricePolicyUseCase updateUC;
    private final DeletePricePolicyUseCase deleteUC;
    private final GetPricePolicyUseCase getUC;
    private final ListPricePolicyUseCase listUC;
    private final ImportBulkPricePolicyUseCase importBulkUC;
    private final ListEligiblePricePolicyUseCase listEligibleUC;

    public PricePolicyController(CreatePricePolicyUseCase createUC, UpdatePricePolicyUseCase updateUC,
                                  DeletePricePolicyUseCase deleteUC, GetPricePolicyUseCase getUC, ListPricePolicyUseCase listUC,
                                  ImportBulkPricePolicyUseCase importBulkUC, ListEligiblePricePolicyUseCase listEligibleUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC; this.importBulkUC = importBulkUC;
        this.listEligibleUC = listEligibleUC;
    }

    // tạo mới chính sách giá
    @PreAuthorize("hasAuthority('pricing.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<PricePolicyResult>> create(@Valid @RequestBody CreatePricePolicyCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    // lấy danh sách chính sách giá
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PricePolicyResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    // lấy danh sách chính sách giá mà khách hàng đang chọn (nếu có) được phép sử dụng — dùng cho
    // dropdown "Chính sách giá" ở form Cơ hội/Báo giá
    @GetMapping("/eligible")
    public ResponseEntity<ApiResponse<List<PricePolicyResult>>> listEligible(
            @RequestParam(required = false) Long customerId) {
        return ResponseEntity.ok(ApiResponse.ok(listEligibleUC.execute(customerId)));
    }

    // lấy chính sách giá theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PricePolicyResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    // cập nhật chính sách giá
    @PreAuthorize("hasAuthority('pricing.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PricePolicyResult>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdatePricePolicyCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdatePricePolicyCommand.builder().id(id).name(cmd.getName()).type(cmd.getType())
                        .priority(cmd.getPriority()).startDate(cmd.getStartDate()).endDate(cmd.getEndDate())
                        .status(cmd.getStatus()).build())));
    }

    // xóa chính sách giá
    @PreAuthorize("hasAuthority('pricing.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }

    // nhập hàng loạt chính sách giá từ file
    @PreAuthorize("hasAuthority('pricing.import')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkPricePolicyCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }
}
