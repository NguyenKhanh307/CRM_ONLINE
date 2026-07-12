package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới dòng đơn hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOrderItemCommand {
    /** ID đơn hàng — controller set từ path; bỏ trống khi tạo nested kèm đơn hàng. */
    private Long orderId;
    private Long productId;
    @Size(max = 20) private String unit;
    @Positive(message = "Số lượng phải lớn hơn 0") private BigDecimal quantity;
    @PositiveOrZero(message = "Đơn giá không được âm") private BigDecimal unitPrice;
    @PositiveOrZero(message = "Chiết khấu không được âm") private BigDecimal discount;
    @DecimalMin(value = "0", message = "Thuế suất phải từ 0 đến 100") @DecimalMax(value = "100", message = "Thuế suất phải từ 0 đến 100") private BigDecimal taxRate;
    private BigDecimal amount;
    @Size(max = 255) private String note;
}
