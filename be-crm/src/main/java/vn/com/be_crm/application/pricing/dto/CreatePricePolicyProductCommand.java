package vn.com.be_crm.application.pricing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.DiscountType;

import java.math.BigDecimal;

/** Input DTO khi tạo mới sản phẩm trong chính sách giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreatePricePolicyProductCommand {
    @NotNull private Long pricePolicyId;
    private Long productId;
    private BigDecimal price;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minQty;
}
