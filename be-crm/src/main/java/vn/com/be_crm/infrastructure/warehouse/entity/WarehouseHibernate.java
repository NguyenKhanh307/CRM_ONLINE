package vn.com.be_crm.infrastructure.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/** Hibernate entity ánh xạ bảng warehouses. */
@Entity @Table(name = "warehouses") @Getter @Setter @NoArgsConstructor
public class WarehouseHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20) private String code;
    @Column(name = "name", nullable = false, length = 40) private String name;
    @Column(name = "address", length = 255) private String address;
    @Column(name = "manager_id") private Long managerId;
    @Column(name = "is_active", nullable = false) private Boolean isActive;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
}
