package vn.com.be_crm.infrastructure.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.order.enums.PaymentScheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng order_payment_schedules.
 */
@Entity
@Table(name = "order_payment_schedules")
@Getter @Setter @NoArgsConstructor
public class OrderPaymentScheduleHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", nullable = false) private Long orderId;
    @Column(name = "installment_no") private Integer installmentNo;
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "amount", precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "paid_amount", precision = 18, scale = 2) private BigDecimal paidAmount;
    @Enumerated(EnumType.STRING) @Column(name = "status", length = 20) private PaymentScheduleStatus status;
    @Column(name = "paid_at") private LocalDateTime paidAt;
    @Column(name = "note", length = 255) private String note;
}
