package vn.com.be_crm.infrastructure.lead.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.lead.enums.LeadActivityType;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng lead_activities.
 */
@Entity
@Table(name = "lead_activities")
@Getter @Setter @NoArgsConstructor
public class LeadActivityHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lead_id", nullable = false)
    private Long leadId;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private LeadActivityType type;
    @Column(name = "subject", nullable = false, length = 30)
    private String subject;
    @Column(name = "content", length = 255)
    private String content;
    @Column(name = "due_at")
    private LocalDateTime dueAt;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    @Column(name = "created_by")
    private Long createdBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
