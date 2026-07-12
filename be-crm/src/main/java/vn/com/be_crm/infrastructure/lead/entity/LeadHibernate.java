package vn.com.be_crm.infrastructure.lead.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.infrastructure.lead.converter.LeadStatusConverter;

import vn.com.be_crm.infrastructure.shared.audit.IAuditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng leads.
 */
@Entity
@Table(name = "leads")
@Getter @Setter @NoArgsConstructor
public class LeadHibernate implements IAuditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "company_name", length = 100)
    private String companyName;
    @Column(name = "lead_type", length = 30)
    private String leadType;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "contact_id")
    private Long contactId;
    @Column(name = "converted_opportunity_id")
    private Long convertedOpportunityId;
    @Column(name = "title", length = 100)
    private String title;
    @Column(name = "department", length = 100)
    private String department;
    @Column(name = "tax_code", length = 15)
    private String taxCode;
    @Column(name = "website", length = 100)
    private String website;
    @Column(name = "industry", length = 50)
    private String industry;
    @Column(name = "source", length = 20)
    private String source;
    @Column(name = "campaign_id")
    private Long campaignId;
    @Convert(converter = LeadStatusConverter.class)
    @Column(name = "status", length = 20)
    private LeadStatus status;
    @Column(name = "estimated_value", precision = 18, scale = 2)
    private BigDecimal estimatedValue;
    @Column(name = "score", nullable = false)
    private Integer score;
    @Column(name = "phone", length = 11)
    private String phone;
    @Column(name = "email", length = 50)
    private String email;
    @Column(name = "do_not_call")
    private boolean doNotCall;
    @Column(name = "do_not_email")
    private boolean doNotEmail;
    @Column(name = "note", length = 255)
    private String note;
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
