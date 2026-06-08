package vn.com.be_crm.domain.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Domain entity đại diện cho nhân viên/đơn vị trong chính sách giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicyEmployee {
    private Long id;
    private Long pricePolicyId;
    private Long userId;
    private Long unitId;
}
