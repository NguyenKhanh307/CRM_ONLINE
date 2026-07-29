package vn.com.be_crm.application.quotation.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Quotation từ file import. */
public record ImportQuotationRowCommand(
        String code,
        Long customerId,
        Long contactId,
        Long opportunityId,
        Long campaignId,
        Long pricePolicyId,
        String quoteDate,
        String validUntil,
        String currency,
        BigDecimal exchangeRate,
        String status,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total,
        String note,
        Long ownerId,
        String ownerEmail
) {}
