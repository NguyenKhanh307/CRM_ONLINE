package vn.com.be_crm.application.opportunity.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Opportunity từ file import. */
public record ImportOpportunityRowCommand(
        String name,
        BigDecimal amount,
        BigDecimal probability,
        String expectedCloseDate,
        String status,
        Long ownerId,
        String ownerEmail
) {}
