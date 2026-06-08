package vn.com.be_crm.domain.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Domain entity đại diện cho danh mục khách hàng trong chính sách giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicyCustomerCategory {
    private Long id;
    private Long pricePolicyId;
    private Long categoryId;
}
