package vn.com.be_crm.infrastructure.lead.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// ánh xạ bảng lead_transfers
@Entity
@Table(name = "lead_transfers")
@Getter @Setter @NoArgsConstructor
public class LeadTransferHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lead_id", nullable = false)
    private Long leadId;
    @Column(name = "from_user_id")
    private Long fromUserId;
    @Column(name = "to_user_id")
    private Long toUserId;
    @Column(name = "reason", length = 500)
    private String reason;
    @CreationTimestamp @Column(name = "transferred_at", updatable = false)
    private LocalDateTime transferredAt;
}
