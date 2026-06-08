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
    private Long id;
    private Long pricePolicyId;
    private Long productId;
    private BigDecimal price;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minQty;
}
