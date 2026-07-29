package vn.com.be_crm.presentation.pricing;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.application.pricing.query.ListPricePolicyProductCategoryUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho danh mục sản phẩm trong chính sách giá — chỉ là marker "chọn nhanh": thêm
 * 1 danh mục thì BE tự bulk-seed toàn bộ sản phẩm thuộc danh mục vào {@code price_policy_products}
 * (giá để trống), sửa giá từng dòng ở tab "Sản phẩm" như bình thường (xem CreatePricePolicyProductCategoryUseCase).
 * Không có endpoint update — marker không mang field nào để sửa. Đổi tên từ
 * PricePolicyCustomerCategoryController (2026-07-29, xem README mục Pricing).
 */
@RestController
@RequestMapping("/api/price-policies/{policyId}/product-categories")
public class PricePolicyProductCategoryController {
    private final CreatePricePolicyProductCategoryUseCase createUC;
    private final DeletePricePolicyProductCategoryUseCase deleteUC;
    private final ListPricePolicyProductCategoryUseCase listUC;

    /** @param createUC tạo mới (bulk-seed) @param deleteUC xóa @param listUC danh sách */
    public PricePolicyProductCategoryController(CreatePricePolicyProductCategoryUseCase createUC,
                                                 DeletePricePolicyProductCategoryUseCase deleteUC,
                                                 ListPricePolicyProductCategoryUseCase listUC) {
        this.createUC = createUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Thêm danh mục sản phẩm (bulk-seed sản phẩm chưa có giá riêng). @param policyId ID chính sách @param cmd body @return 201 */
    @PreAuthorize("hasAuthority('pricing.edit')")
    @PostMapping
    public ResponseEntity<ApiResponse<PricePolicyProductCategoryResult>> create(@PathVariable Long policyId,
                                                                                  @Valid @RequestBody CreatePricePolicyProductCategoryCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreatePricePolicyProductCategoryCommand.builder().pricePolicyId(policyId).categoryId(cmd.getCategoryId()).build())));
    }

    /** Lấy danh sách danh mục sản phẩm. @param policyId ID chính sách @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PricePolicyProductCategoryResult>>> list(@PathVariable Long policyId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(policyId)));
    }

    /** Xóa danh mục sản phẩm (chỉ xóa marker, giữ nguyên các dòng sản phẩm đã bulk-seed). @param id ID @return 204 */
    @PreAuthorize("hasAuthority('pricing.edit')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long policyId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
