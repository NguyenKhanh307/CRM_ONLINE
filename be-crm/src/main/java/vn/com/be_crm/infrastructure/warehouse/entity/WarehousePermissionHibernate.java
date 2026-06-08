package vn.com.be_crm.infrastructure.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import vn.com.be_crm.domain.warehouse.enums.WarehousePermissionType;

import java.time.LocalDateTime;

/** Hibernate entity ánh xạ bảng warehouse_permissions. */
@Entity @Table(name = "warehouse_permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"warehouse_id", "user_id"}))
@Getter @Setter @NoArgsConstructor
public class WarehousePermissionHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "warehouse_id", nullable = false) private Long warehouseId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Enumerated(EnumType.STRING) @Column(name = "permission", nullable = false, length = 10) private WarehousePermissionType permission;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
}
