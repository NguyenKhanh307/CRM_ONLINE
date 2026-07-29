package vn.com.be_crm.application.invoice.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Invoice từ file import. */
public record ImportInvoiceRowCommand(
        String code,
        Long customerId,
        Long contactId,
        Long quotationId,
        Long opportunityId,
        Long orderId,
        Long campaignId,
        String invoiceDate,
        String dueDate,
        String currency,
        BigDecimal exchangeRate,
        String status,
        String paymentStatus,
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
