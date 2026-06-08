package vn.com.be_crm.infrastructure.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng role_permissions.
 */
@Entity
@Table(name = "role_permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "permission_id"}))
@Getter
@Setter
@NoArgsConstructor
public class RolePermissionHibernate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
