package vn.com.be_crm.infrastructure.invoice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;

import vn.com.be_crm.core.audit.IAuditable;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ánh xạ bảng invoices
@Entity
@Table(name = "invoices")
@Getter @Setter @NoArgsConstructor
public class InvoiceHibernate implements IAuditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, length = 20) private String code;
    @Column(name = "order_id") private Long orderId;
    @Column(name = "owner_id") private Long ownerId;
    @Column(name = "invoice_date") private LocalDate invoiceDate;
    @Column(name = "due_date") private LocalDate dueDate;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private InvoiceStatus status;
    @Enumerated(EnumType.STRING) @Column(name = "payment_status", length = 20) private PaymentStatus paymentStatus;
    @Column(name = "is_locked") private boolean isLocked;
    @Column(name = "note", length = 255) private String note;
    // updatable = false: created_by không bao giờ vào câu UPDATE → merge() không thể NULL đè
    @Column(name = "created_by", updatable = false) private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
