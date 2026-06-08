package vn.com.be_crm.infrastructure.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng roles.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RoleHibernate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "description", length = 50)
    private String description;

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
