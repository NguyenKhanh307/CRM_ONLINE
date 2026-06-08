package vn.com.be_crm.application.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.DiscountType;

import java.math.BigDecimal;

/** Input DTO khi cập nhật sản phẩm trong chính sách giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdatePricePolicyProductCommand {
    private Long id;
    private BigDecimal price;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minQty;
}
