package vn.com.be_crm.infrastructure.lead.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import vn.com.be_crm.domain.lead.enums.LeadItemInterestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ánh xạ bảng lead_items
@Entity
@Table(name = "lead_items")
@Getter @Setter @NoArgsConstructor
public class LeadItemHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lead_id", nullable = false)
    private Long leadId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal quantity;
    @Enumerated(EnumType.STRING)
    @Column(name = "interest_type", length = 20, nullable = false)
    private LeadItemInterestType interestType;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
