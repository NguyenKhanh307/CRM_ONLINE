package vn.com.be_crm.application.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Output DTO cho InvoiceItem. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoiceItemResult {
    private Long id;
    private Long invoiceId;
    private Long productId;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    private String note;
}
