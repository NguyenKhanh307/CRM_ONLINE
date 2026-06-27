package vn.com.be_crm.infrastructure.invoice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng invoice_revenue_records.
 */
@Entity
@Table(name = "invoice_revenue_records")
@Getter @Setter @NoArgsConstructor
public class InvoiceRevenueRecordHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "invoice_id", nullable = false) private Long invoiceId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "revenue_amount", precision = 18, scale = 2) private BigDecimal revenueAmount;
    @Column(name = "percentage", precision = 5, scale = 2) private BigDecimal percentage;
    @Column(name = "note", length = 255) private String note;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}
