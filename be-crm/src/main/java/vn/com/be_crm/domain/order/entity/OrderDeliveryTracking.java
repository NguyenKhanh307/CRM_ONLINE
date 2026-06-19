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
    /** ID bản ghi theo dõi giao hàng. */
    private Long id;
    /** ID đơn hàng. */
    private Long orderId;
    /** Trạng thái giao hàng. */
    private DeliveryStatus status;
    /** Đơn vị vận chuyển. */
    private String carrier;
    /** Mã vận đơn. */
    private String trackingNo;
    /** Thời điểm xuất hàng. */
    private LocalDateTime shippedAt;
    /** Thời điểm giao thành công. */
    private LocalDateTime deliveredAt;
    /** Ghi chú. */
    private String note;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
