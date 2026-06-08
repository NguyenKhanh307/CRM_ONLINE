package vn.com.be_crm.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho bản ghi doanh thu đơn hàng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRevenueRecord {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal revenueAmount;
    private BigDecimal percentage;
    private String note;
    private LocalDateTime createdAt;
}
