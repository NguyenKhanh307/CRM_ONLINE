package vn.com.be_crm.infrastructure.campaign.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.enums.CampaignType;

import vn.com.be_crm.core.audit.IAuditable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng campaigns.
 */
@Entity
@Table(name = "campaigns")
@Getter @Setter @NoArgsConstructor
public class CampaignHibernate implements IAuditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20) private String code;
    @Column(name = "name", nullable = false, length = 150) private String name;
    @Enumerated(EnumType.STRING) @Column(name = "type", length = 20) private CampaignType type;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private CampaignStatus status;
    @Column(name = "channel", length = 50) private String channel;
    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "end_date") private LocalDate endDate;
    @Column(name = "budget", precision = 18, scale = 2) private BigDecimal budget;
    @Column(name = "actual_cost", precision = 18, scale = 2) private BigDecimal actualCost;
    @Column(name = "target_size") private Integer targetSize;
    @Column(name = "expected_revenue", precision = 18, scale = 2) private BigDecimal expectedRevenue;
    @Column(name = "owner_id") private Long ownerId;
    @Column(name = "description", length = 500) private String description;
    // updatable = false: created_by không bao giờ vào câu UPDATE → merge() không thể NULL đè
    @Column(name = "created_by", updatable = false) private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
