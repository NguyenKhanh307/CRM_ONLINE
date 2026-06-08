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
    private Long id;
    private Long pricePolicyId;
    private Long productTypeId;
}
