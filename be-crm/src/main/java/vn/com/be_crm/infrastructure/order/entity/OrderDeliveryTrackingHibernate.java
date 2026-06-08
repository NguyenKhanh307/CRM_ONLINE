package vn.com.be_crm.infrastructure.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.order.enums.DeliveryStatus;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng order_delivery_tracking.
 */
@Entity
@Table(name = "order_delivery_tracking")
@Getter @Setter @NoArgsConstructor
public class OrderDeliveryTrackingHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", nullable = false) private Long orderId;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private DeliveryStatus status;
    @Column(name = "carrier", length = 100) private String carrier;
    @Column(name = "tracking_no", length = 100) private String trackingNo;
    @Column(name = "shipped_at") private LocalDateTime shippedAt;
    @Column(name = "delivered_at") private LocalDateTime deliveredAt;
    @Column(name = "note", length = 500) private String note;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
}
