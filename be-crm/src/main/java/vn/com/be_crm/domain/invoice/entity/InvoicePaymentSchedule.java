package vn.com.be_crm.domain.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

// đợt thanh toán hóa đơn
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePaymentSchedule {
    private Long id;
    private Long invoiceId;
    private Integer installmentNo;
    private LocalDate dueDate;
    // số tiền phải trả đợt này
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private PaymentScheduleStatus status;
    private String bankName;
    private String bankAccount;
    private String note;
}
