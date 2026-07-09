package vn.com.be_crm.application.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho Invoice. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoiceResult {
    private Long id;
    private String code;
    private Long customerId;
    private Long contactId;
    private Long quotationId;
    private Long opportunityId;
    private Long orderId;
    private Long campaignId;
    private Long ownerId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String currency;
    private BigDecimal exchangeRate;
    private InvoiceStatus status;
    private PaymentStatus paymentStatus;
    private Boolean isLocked;
    private String billingAddress;
    private String taxCode;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver).
    private String customerName;
    private String contactName;
    private String quotationCode;
    private String opportunityName;
    private String ownerName;
}
