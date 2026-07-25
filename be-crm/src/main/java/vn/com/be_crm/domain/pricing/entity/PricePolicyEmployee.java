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
    /** ID dòng. */
    private Long id;
    /** ID chính sách giá. */
    private Long pricePolicyId;
    /** ID nhân viên được dùng chính sách. */
    private Long userId;
}
