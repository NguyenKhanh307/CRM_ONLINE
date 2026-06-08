package vn.com.be_crm.infrastructure.lead.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.infrastructure.lead.converter.LeadStatusConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng leads.
 */
@Entity
@Table(name = "leads")
@Getter @Setter @NoArgsConstructor
public class LeadHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "contact_id")
    private Long contactId;
    @Column(name = "source", length = 20)
    private String source;
    @Convert(converter = LeadStatusConverter.class)
    @Column(name = "status", length = 20)
    private LeadStatus status;
    @Column(name = "estimated_value", precision = 18, scale = 2)
    private BigDecimal estimatedValue;
    @Column(name = "phone", length = 11)
    private String phone;
    @Column(name = "email", length = 50)
    private String email;
    @Column(name = "note", length = 255)
    private String note;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
