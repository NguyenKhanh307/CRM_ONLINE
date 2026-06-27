package vn.com.be_crm.application.invoice.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Invoice từ file import. */
public record ImportInvoiceRowCommand(
        String code,
        String invoiceDate,
        String status,
        String paymentStatus,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total,
        String note,
        Long ownerId,
        String ownerEmail
) {}
