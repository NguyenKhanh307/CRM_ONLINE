package vn.com.be_crm.infrastructure.opportunity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Hibernate entity ánh xạ bảng opportunity_stages. */
@Entity @Table(name = "opportunity_stages") @Getter @Setter @NoArgsConstructor
public class OpportunityStageHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "name", nullable = false, length = 40) private String name;
    @Column(name = "sort_order", nullable = false) private Integer sortOrder;
    @Column(name = "probability", nullable = false, precision = 5, scale = 2) private BigDecimal probability;
    @Column(name = "is_won", nullable = false) private Boolean isWon;
    @Column(name = "is_lost", nullable = false) private Boolean isLost;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
}
