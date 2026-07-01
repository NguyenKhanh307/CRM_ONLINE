package vn.com.be_crm.infrastructure.service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.service.enums.ResolutionType;
import vn.com.be_crm.domain.service.enums.ReturnReason;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.enums.TicketType;
import vn.com.be_crm.infrastructure.service.converter.TicketStatusConverter;
import vn.com.be_crm.infrastructure.service.converter.TicketTypeConverter;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng support_tickets.
 */
@Entity
@Table(name = "support_tickets")
@Getter @Setter @NoArgsConstructor
public class TicketHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20) private String code;
    @Convert(converter = TicketTypeConverter.class)
    @Column(name = "type", nullable = false, length = 20) private TicketType type;
    @Column(name = "subject", nullable = false, length = 150) private String subject;
    @Column(name = "description", length = 1000) private String description;
    @Column(name = "customer_id") private Long customerId;
    @Column(name = "contact_id") private Long contactId;
    @Column(name = "invoice_id") private Long invoiceId;
    @Column(name = "product_id") private Long productId;
    @Enumerated(EnumType.STRING) @Column(name = "channel", length = 10) private TicketChannel channel;
    @Enumerated(EnumType.STRING) @Column(name = "priority", length = 10) private TicketPriority priority;
    @Convert(converter = TicketStatusConverter.class)
    @Column(name = "status", length = 20) private TicketStatus status;
    @Enumerated(EnumType.STRING) @Column(name = "reason", length = 20) private ReturnReason reason;
    @Enumerated(EnumType.STRING) @Column(name = "resolution_type", length = 20) private ResolutionType resolutionType;
    @Column(name = "resolution_note", length = 255) private String resolutionNote;
    @Column(name = "assigned_user_id") private Long assignedUserId;
    @Column(name = "sla_policy_id") private Long slaPolicyId;
    @Column(name = "first_response_at") private LocalDateTime firstResponseAt;
    @Column(name = "sla_due_at") private LocalDateTime slaDueAt;
    @Column(name = "resolved_at") private LocalDateTime resolvedAt;
    @Column(name = "closed_at") private LocalDateTime closedAt;
    @Column(name = "satisfaction_score") private Integer satisfactionScore;
    @Column(name = "satisfaction_comment", length = 255) private String satisfactionComment;
    @Column(name = "created_by") private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
