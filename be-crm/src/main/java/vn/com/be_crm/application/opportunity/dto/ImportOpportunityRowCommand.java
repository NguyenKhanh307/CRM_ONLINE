package vn.com.be_crm.application.opportunity.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Opportunity từ file import. */
public record ImportOpportunityRowCommand(
        String code,
        String name,
        String opportunityType,
        Long customerId,
        Long contactId,
        Long stageId,
        Long pricePolicyId,
        BigDecimal amount,
        BigDecimal expectedRevenue,
        BigDecimal probability,
        String expectedCloseDate,
        String source,
        Long campaignId,
        String winLossReason,
        String description,
        String status,
        Long ownerId,
        String ownerEmail
) {}
