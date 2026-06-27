package vn.com.be_crm.application.invoice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.invoice.enums.PaymentScheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Input DTO khi cập nhật đợt thanh toán. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateInvoicePaymentScheduleCommand {
    private Long id;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private PaymentScheduleStatus status;
    private LocalDateTime paidAt;
    @Size(max = 255) private String note;
}
