package vn.com.be_crm.domain.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho đợt thanh toán đơn hàng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePaymentSchedule {
    /** ID đợt thanh toán. */
    private Long id;
    /** ID đơn hàng. */
    private Long invoiceId;
    /** Số thứ tự đợt thanh toán. */
    private Integer installmentNo;
    /** Hạn thanh toán. */
    private LocalDate dueDate;
    /** Số tiền phải trả đợt này. */
    private BigDecimal amount;
    /** Số tiền đã trả. */
    private BigDecimal paidAmount;
    /** Trạng thái đợt thanh toán. */
    private PaymentScheduleStatus status;
    /** Thời điểm thanh toán. */
    private LocalDateTime paidAt;
    /** Ghi chú. */
    private String note;
}
