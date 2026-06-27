package vn.com.be_crm.application.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Output DTO cho InvoiceRevenueRecord. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoiceRevenueRecordResult {
    private Long id;
    private Long invoiceId;
    private Long userId;
    private BigDecimal revenueAmount;
    private BigDecimal percentage;
    private String note;
    private LocalDateTime createdAt;
}
