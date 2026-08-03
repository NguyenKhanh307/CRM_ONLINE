package vn.com.be_crm.presentation.pricing;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.*;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.page.PageResponse;

import java.util.List;

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
    private final ImportBulkPricePolicyUseCase importBulkUC;
    private final ListEligiblePricePolicyUseCase listEligibleUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách @param importBulkUC nhập hàng loạt @param listEligibleUC danh sách hợp lệ cho form */
    public PricePolicyController(CreatePricePolicyUseCase createUC, UpdatePricePolicyUseCase updateUC,
                                  DeletePricePolicyUseCase deleteUC, GetPricePolicyUseCase getUC, ListPricePolicyUseCase listUC,
                                  ImportBulkPricePolicyUseCase importBulkUC, ListEligiblePricePolicyUseCase listEligibleUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC; this.importBulkUC = importBulkUC;
        this.listEligibleUC = listEligibleUC;
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

    /**
     * Lấy danh sách chính sách giá mà người đang đăng nhập (và khách hàng đang chọn, nếu có) được
     * phép sử dụng — dùng cho dropdown "Chính sách giá" ở form Cơ hội/Báo giá. ADMIN/SALES_MANAGER
     * luôn thấy mọi chính sách.
     * @param customerId ID khách hàng đang chọn trên form (tùy chọn) @param req request hiện tại
     * @return 200
     */
    @GetMapping("/eligible")
    public ResponseEntity<ApiResponse<List<PricePolicyResult>>> listEligible(
            @RequestParam(required = false) Long customerId, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        boolean privileged = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(listEligibleUC.execute(userId, privileged, customerId)));
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

    /** Nhập hàng loạt chính sách giá từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('pricing.import')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkPricePolicyCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }
}
