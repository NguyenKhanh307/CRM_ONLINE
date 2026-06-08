package vn.com.be_crm.domain.opportunity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Domain entity đại diện cho dòng sản phẩm trong cơ hội.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityItem {
    private Long id;
    private Long opportunityId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal amount;
    private String note;
}
