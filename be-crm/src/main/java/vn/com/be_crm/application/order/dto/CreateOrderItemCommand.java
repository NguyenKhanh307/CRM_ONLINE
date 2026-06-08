package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới dòng đơn hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOrderItemCommand {
    @NotNull private Long orderId;
    private Long productId;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    @Size(max = 255) private String note;
}
