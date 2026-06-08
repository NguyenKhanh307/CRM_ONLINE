package vn.com.be_crm.infrastructure.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import vn.com.be_crm.domain.customer.enums.CustomerSharePermission;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng customer_shares.
 */
@Entity
@Table(name = "customer_shares")
@Getter @Setter @NoArgsConstructor
public class CustomerShareHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "permission", length = 10)
    private CustomerSharePermission permission;
    @Column(name = "shared_by")
    private Long sharedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
