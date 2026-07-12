package vn.com.be_crm.application.pricing.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    @PositiveOrZero(message = "Đơn giá không được âm") private BigDecimal price;
    private DiscountType discountType;
    @PositiveOrZero(message = "Giá trị chiết khấu không được âm") private BigDecimal discountValue;
    @Positive(message = "Số lượng phải lớn hơn 0") private BigDecimal minQty;
}
