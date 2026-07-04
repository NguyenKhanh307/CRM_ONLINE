package vn.com.be_crm.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Output DTO cho OrderItem. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderItemResult {
    private Long id;
    private Long orderId;
    private Long productId;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    private String note;
}
