package vn.com.be_crm.infrastructure.quotation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import vn.com.be_crm.domain.quotation.enums.QuotationApprovalStatus;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng quotation_approvals.
 */
@Entity
@Table(name = "quotation_approvals")
@Getter @Setter @NoArgsConstructor
public class QuotationApprovalHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "quotation_id", nullable = false) private Long quotationId;
    @Column(name = "approver_id") private Long approverId;
    @Column(name = "level") private Integer level;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20)
    private QuotationApprovalStatus status;
    @Column(name = "comment", length = 500) private String comment;
    @Column(name = "approved_at") private LocalDateTime approvedAt;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}
