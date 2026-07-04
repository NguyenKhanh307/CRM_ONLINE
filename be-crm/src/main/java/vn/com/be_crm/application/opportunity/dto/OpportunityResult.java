package vn.com.be_crm.application.opportunity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho Opportunity. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OpportunityResult {
    private Long id;
    private String code;
    private String name;
    private String opportunityType;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long stageId;
    private Long pricePolicyId;
    private BigDecimal amount;
    private BigDecimal expectedRevenue;
    private BigDecimal probability;
    private LocalDate expectedCloseDate;
    private String source;
    private Long campaignId;
    private String winLossReason;
    private String description;
    private OpportunityStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
