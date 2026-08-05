package vn.com.be_crm.application.opportunity.dto;

import java.math.BigDecimal;

// một dòng dữ liệu Opportunity từ file import
public record ImportOpportunityRowCommand(
        String code,
        String name,
        String opportunityType,
        Long customerId,
        Long contactId,
        Long stageId,
        Long pricePolicyId,
        BigDecimal amount,
        String source,
        Long campaignId,
        String winLossReason,
        String description,
        String status,
        Long ownerId,
        String ownerEmail
) {}
