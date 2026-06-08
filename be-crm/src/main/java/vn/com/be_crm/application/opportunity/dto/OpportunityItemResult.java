package vn.com.be_crm.application.opportunity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Output DTO cho OpportunityItem. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OpportunityItemResult {
    private Long id;
    private Long opportunityId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal amount;
    private String note;
}
