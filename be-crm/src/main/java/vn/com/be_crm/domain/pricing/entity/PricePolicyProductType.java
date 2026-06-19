package vn.com.be_crm.domain.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Domain entity đại diện cho loại sản phẩm trong chính sách giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicyProductType {
    /** ID dòng. */
    private Long id;
    /** ID chính sách giá. */
    private Long pricePolicyId;
    /** ID loại/danh mục sản phẩm được áp dụng. */
    private Long productTypeId;
}
