package vn.com.be_crm.domain.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Domain entity đại diện cho danh mục sản phẩm trong chính sách giá.
 * Đổi tên từ PricePolicyCustomerCategory (2026-07-29) — tên cũ sai bản chất: category_id vốn đã
 * tham chiếu product_categories, không hề có khái niệm phân khúc khách hàng nào trong DB.
 *
 * <p>Đây chỉ là marker "chọn nhanh" — không mang giá/chiết khấu. Chọn 1 danh mục thì
 * {@code CreatePricePolicyProductCategoryUseCase} bulk-seed toàn bộ sản phẩm thuộc danh mục vào
 * {@code price_policy_products} (giá để trống), rồi sửa giá từng dòng như bình thường.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicyProductCategory {
    /** ID dòng. */
    private Long id;
    /** ID chính sách giá. */
    private Long pricePolicyId;
    /** ID danh mục sản phẩm được áp dụng. */
    private Long categoryId;
}
