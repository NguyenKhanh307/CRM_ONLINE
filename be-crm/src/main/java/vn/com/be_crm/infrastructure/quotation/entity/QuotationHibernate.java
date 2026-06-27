package vn.com.be_crm.infrastructure.quotation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng quotations.
 */
@Entity
@Table(name = "quotations")
@Getter @Setter @NoArgsConstructor
public class QuotationHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    @Column(name = "customer_id") private Long customerId;
    @Column(name = "contact_id") private Long contactId;
    @Column(name = "opportunity_id") private Long opportunityId;
    @Column(name = "price_policy_id") private Long pricePolicyId;
    @Column(name = "is_primary") private boolean isPrimary;
    @Column(name = "is_locked") private boolean isLocked;
    @Column(name = "owner_id") private Long ownerId;
    @Column(name = "quote_date") private LocalDate quoteDate;
    @Column(name = "valid_until") private LocalDate validUntil;
    @Column(name = "currency", length = 3) private String currency;
    @Column(name = "exchange_rate", precision = 18, scale = 6) private java.math.BigDecimal exchangeRate;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20)
    private QuotationStatus status;
    @Column(name = "subtotal", precision = 18, scale = 2) private BigDecimal subtotal;
    @Column(name = "discount", precision = 18, scale = 2) private BigDecimal discount;
    @Column(name = "tax", precision = 18, scale = 2) private BigDecimal tax;
    @Column(name = "total", precision = 18, scale = 2) private BigDecimal total;
    @Column(name = "note", length = 255) private String note;
    @Column(name = "created_by") private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
