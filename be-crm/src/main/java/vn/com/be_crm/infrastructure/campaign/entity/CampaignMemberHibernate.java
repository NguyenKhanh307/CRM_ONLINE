package vn.com.be_crm.infrastructure.campaign.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.campaign.enums.CampaignMemberStatus;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng campaign_members.
 */
@Entity
@Table(name = "campaign_members")
@Getter @Setter @NoArgsConstructor
public class CampaignMemberHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "campaign_id", nullable = false) private Long campaignId;
    @Column(name = "lead_id") private Long leadId;
    @Column(name = "contact_id") private Long contactId;
    @Column(name = "name", length = 100) private String name;
    @Column(name = "email", length = 100) private String email;
    @Column(name = "phone", length = 20) private String phone;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private CampaignMemberStatus status;
    @Column(name = "sent_at") private LocalDateTime sentAt;
    @Column(name = "responded_at") private LocalDateTime respondedAt;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
}
