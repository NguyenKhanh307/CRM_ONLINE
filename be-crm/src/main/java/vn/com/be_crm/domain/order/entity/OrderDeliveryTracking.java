package vn.com.be_crm.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.DeliveryStatus;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho theo dõi giao hàng đơn hàng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryTracking {
    private Long id;
    private Long orderId;
    private DeliveryStatus status;
    private String carrier;
    private String trackingNo;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
