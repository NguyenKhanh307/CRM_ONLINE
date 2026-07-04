package vn.com.be_crm.application.invoice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi cập nhật hóa đơn. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateInvoiceCommand {
    private Long id;
    private Long customerId;
    private Long contactId;
    private Long quotationId;
    private Long opportunityId;
    private Long orderId;
    private Long campaignId;
    private Long ownerId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    @Size(max = 3) private String currency;
    private BigDecimal exchangeRate;
    @Size(max = 255) private String billingAddress;
    @Size(max = 15) private String taxCode;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    @Size(max = 255) private String note;
}
