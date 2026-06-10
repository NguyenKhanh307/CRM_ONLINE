package vn.com.be_crm.application.quotation.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Quotation từ file import. */
public record ImportQuotationRowCommand(
        String quoteDate,
        String validUntil,
        String status,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total,
        String note,
        Long ownerId,
        String ownerEmail
) {}
