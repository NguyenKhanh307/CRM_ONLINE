package vn.com.be_crm.infrastructure.lead.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng lead_tracking_events.
 */
@Entity
@Table(name = "lead_tracking_events")
@Getter @Setter @NoArgsConstructor
public class LeadTrackingEventHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lead_id", nullable = false)
    private Long leadId;
    @Column(name = "action", length = 50)
    private String action;
    @Column(name = "label", length = 100)
    private String label;
    @Column(name = "points", nullable = false)
    private Integer points;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
