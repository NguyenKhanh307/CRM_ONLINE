package vn.com.be_crm.infrastructure.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.enums.OrderType;
import vn.com.be_crm.domain.order.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng orders.
 */
@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class OrderHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20) private String code;
    @Column(name = "customer_id") private Long customerId;
    @Column(name = "contact_id") private Long contactId;
    @Column(name = "owner_id") private Long ownerId;
    @Column(name = "executor_unit_id") private Long executorUnitId;
    @Column(name = "warehouse_id") private Long warehouseId;
    @Column(name = "parent_order_id") private Long parentOrderId;
    @Enumerated(EnumType.STRING) @Column(name = "order_type", length = 20) private OrderType orderType;
    @Column(name = "order_date") private LocalDate orderDate;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private OrderStatus status;
    @Enumerated(EnumType.STRING) @Column(name = "payment_status", length = 20) private PaymentStatus paymentStatus;
    @Column(name = "subtotal", precision = 18, scale = 2) private BigDecimal subtotal;
    @Column(name = "discount", precision = 18, scale = 2) private BigDecimal discount;
    @Column(name = "tax", precision = 18, scale = 2) private BigDecimal tax;
    @Column(name = "total", precision = 18, scale = 2) private BigDecimal total;
    @Column(name = "note", length = 255) private String note;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
