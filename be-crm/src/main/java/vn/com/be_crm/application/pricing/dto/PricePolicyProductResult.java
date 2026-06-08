package vn.com.be_crm.application.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.DiscountType;

import java.math.BigDecimal;

/** Output DTO cho PricePolicyProduct. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class PricePolicyProductResult {
    private Long id;
    private Long pricePolicyId;
    private Long productId;
    private BigDecimal price;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minQty;
}
