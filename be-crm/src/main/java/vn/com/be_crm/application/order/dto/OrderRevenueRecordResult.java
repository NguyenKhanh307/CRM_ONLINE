package vn.com.be_crm.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Output DTO cho OrderRevenueRecord. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderRevenueRecordResult {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal revenueAmount;
    private BigDecimal percentage;
    private String note;
    private LocalDateTime createdAt;
}
