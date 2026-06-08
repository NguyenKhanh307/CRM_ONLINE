package vn.com.be_crm.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.PaymentScheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho OrderPaymentSchedule. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderPaymentScheduleResult {
    private Long id;
    private Long orderId;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private PaymentScheduleStatus status;
    private LocalDateTime paidAt;
    private String note;
}
