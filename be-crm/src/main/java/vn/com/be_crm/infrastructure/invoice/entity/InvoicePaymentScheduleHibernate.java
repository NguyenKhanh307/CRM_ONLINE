package vn.com.be_crm.infrastructure.invoice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

// ánh xạ bảng invoice_payment_schedules
@Entity
@Table(name = "invoice_payment_schedules")
@Getter @Setter @NoArgsConstructor
public class InvoicePaymentScheduleHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "invoice_id", nullable = false) private Long invoiceId;
    @Column(name = "installment_no") private Integer installmentNo;
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "amount", precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "paid_amount", precision = 18, scale = 2) private BigDecimal paidAmount;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private PaymentScheduleStatus status;
    @Column(name = "bank_name", length = 100) private String bankName;
    @Column(name = "bank_account", length = 50) private String bankAccount;
    @Column(name = "note", length = 255) private String note;
}
