package vn.com.be_crm.application.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

// output cho InvoicePaymentSchedule
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoicePaymentScheduleResult {
    private Long id;
    private Long invoiceId;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private PaymentScheduleStatus status;
    private String bankName;
    private String bankAccount;
    private String note;
}
