package vn.com.be_crm.domain.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Domain entity đại diện cho khách hàng trong chính sách giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicyCustomer {
    /** ID dòng. */
    private Long id;
    /** ID chính sách giá. */
    private Long pricePolicyId;
    /** ID khách hàng được áp dụng chính sách. */
    private Long customerId;
}
