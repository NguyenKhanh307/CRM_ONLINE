package vn.com.be_crm.infrastructure.pricing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng price_policies.
 */
@Entity
@Table(name = "price_policies")
@Getter @Setter @NoArgsConstructor
public class PricePolicyHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20) private String code;
    @Column(name = "name", nullable = false, length = 40) private String name;
    @Column(name = "type", length = 20) private String type;
    @Column(name = "priority") private Integer priority;
    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "end_date") private LocalDate endDate;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private PricePolicyStatus status;
    @Column(name = "created_by") private Long createdBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
}
