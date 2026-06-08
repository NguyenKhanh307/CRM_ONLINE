package vn.com.be_crm.infrastructure.opportunity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng opportunities.
 */
@Entity
@Table(name = "opportunities")
@Getter @Setter @NoArgsConstructor
public class OpportunityHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name = "name", nullable = false, length = 40)
    private String name;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "contact_id")
    private Long contactId;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "stage_id")
    private Long stageId;
    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;
    @Column(name = "probability", precision = 5, scale = 2)
    private BigDecimal probability;
    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private OpportunityStatus status;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
