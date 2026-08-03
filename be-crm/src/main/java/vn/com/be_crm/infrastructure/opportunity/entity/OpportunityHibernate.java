package vn.com.be_crm.infrastructure.opportunity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import vn.com.be_crm.core.audit.IAuditable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng opportunities.
 */
@Entity
@Table(name = "opportunities")
@Getter @Setter @NoArgsConstructor
public class OpportunityHibernate implements IAuditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name = "name", nullable = false, length = 40)
    private String name;
    @Column(name = "opportunity_type", length = 20)
    private String opportunityType;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "contact_id")
    private Long contactId;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "stage_id")
    private Long stageId;
    @Column(name = "price_policy_id")
    private Long pricePolicyId;
    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;
    @Column(name = "expected_revenue", precision = 18, scale = 2)
    private BigDecimal expectedRevenue;
    @Column(name = "probability", precision = 5, scale = 2)
    private BigDecimal probability;
    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;
    @Column(name = "source", length = 30)
    private String source;
    @Column(name = "campaign_id")
    private Long campaignId;
    @Column(name = "win_loss_reason", length = 255)
    private String winLossReason;
    @Column(name = "description", length = 500)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private OpportunityStatus status;
    // updatable = false: created_by không bao giờ vào câu UPDATE → merge() không thể NULL đè
    @Column(name = "created_by", updatable = false) private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
