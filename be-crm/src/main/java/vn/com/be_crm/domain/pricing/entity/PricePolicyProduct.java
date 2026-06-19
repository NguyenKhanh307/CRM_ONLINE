package vn.com.be_crm.domain.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.DiscountType;

import java.math.BigDecimal;

/**
 * Domain entity đại diện cho sản phẩm trong chính sách giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicyProduct {
    /** ID dòng. */
    private Long id;
    /** ID chính sách giá. */
    private Long pricePolicyId;
    /** ID sản phẩm áp dụng. */
    private Long productId;
    /** Giá áp dụng cho sản phẩm. */
    private BigDecimal price;
    /** Kiểu chiết khấu (theo % hoặc số tiền). */
    private DiscountType discountType;
    /** Giá trị chiết khấu. */
    private BigDecimal discountValue;
    /** Số lượng tối thiểu để được áp dụng. */
    private BigDecimal minQty;
}
