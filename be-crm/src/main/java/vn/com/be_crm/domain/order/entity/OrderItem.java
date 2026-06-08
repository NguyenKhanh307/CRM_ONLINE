package vn.com.be_crm.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Domain entity đại diện cho dòng sản phẩm trong đơn hàng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    private String note;
}
