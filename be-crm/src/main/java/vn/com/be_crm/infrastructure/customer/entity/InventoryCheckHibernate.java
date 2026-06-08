package vn.com.be_crm.infrastructure.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.customer.enums.InventoryCheckStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng inventory_checks.
 */
@Entity
@Table(name = "inventory_checks")
@Getter @Setter @NoArgsConstructor
public class InventoryCheckHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    @Column(name = "checked_by")
    private Long checkedBy;
    @Column(name = "check_date")
    private LocalDate checkDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private InventoryCheckStatus status;
    @Column(name = "note", length = 255)
    private String note;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
