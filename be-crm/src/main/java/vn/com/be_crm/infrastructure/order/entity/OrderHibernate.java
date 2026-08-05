package vn.com.be_crm.infrastructure.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.order.enums.OrderStatus;

import vn.com.be_crm.core.audit.IAuditable;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ánh xạ bảng orders
@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class OrderHibernate implements IAuditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20) private String code;
    @Column(name = "quotation_id") private Long quotationId;
    @Column(name = "owner_id") private Long ownerId;
    @Column(name = "order_date") private LocalDate orderDate;
    @Column(name = "delivery_date") private LocalDate deliveryDate;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private OrderStatus status;
    @Column(name = "is_locked") private boolean isLocked;
    @Column(name = "note", length = 255) private String note;
    // updatable = false: created_by không bao giờ vào câu UPDATE → merge() không thể NULL đè
    @Column(name = "created_by", updatable = false) private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
