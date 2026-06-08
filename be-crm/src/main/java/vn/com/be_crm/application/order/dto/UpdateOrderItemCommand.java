package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi cập nhật dòng đơn hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateOrderItemCommand {
    private Long id;
    private Long productId;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    @Size(max = 255) private String note;
}
