package vn.com.be_crm.application.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Output DTO cho QuotationItem. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class QuotationItemResult {
    private Long id;
    private Long quotationId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    private String note;
}
