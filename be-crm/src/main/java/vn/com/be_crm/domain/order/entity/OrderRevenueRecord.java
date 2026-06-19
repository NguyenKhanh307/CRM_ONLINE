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
    /** ID bản ghi doanh thu. */
    private Long id;
    /** ID đơn hàng. */
    private Long orderId;
    /** ID nhân viên được ghi nhận doanh số. */
    private Long userId;
    /** Doanh số ghi nhận. */
    private BigDecimal revenueAmount;
    /** Tỷ lệ chia doanh số (%). */
    private BigDecimal percentage;
    /** Ghi chú. */
    private String note;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
}
