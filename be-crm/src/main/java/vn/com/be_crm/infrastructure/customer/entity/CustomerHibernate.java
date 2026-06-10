package vn.com.be_crm.infrastructure.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng customers.
 */
@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor
public class CustomerHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private CustomerType type;
    @Column(name = "tax_code", length = 15)
    private String taxCode;
    @Column(name = "phone", length = 11)
    private String phone;
    @Column(name = "email", length = 50)
    private String email;
    @Column(name = "address", length = 255)
    private String address;
    @Column(name = "source", length = 20)
    private String source;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private CustomerStatus status;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "unit_id")
    private Long unitId;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
