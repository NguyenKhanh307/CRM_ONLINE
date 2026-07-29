package vn.com.be_crm.application.order.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Order từ file import. */
public record ImportOrderRowCommand(
        String code,
        Long customerId,
        Long contactId,
        Long quotationId,
        Long opportunityId,
        Long campaignId,
        String orderDate,
        String deliveryDate,
        String currency,
        BigDecimal exchangeRate,
        String status,
        String billingAddress,
        String taxCode,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total,
        String note,
        Long ownerId,
        String ownerEmail
) {}
