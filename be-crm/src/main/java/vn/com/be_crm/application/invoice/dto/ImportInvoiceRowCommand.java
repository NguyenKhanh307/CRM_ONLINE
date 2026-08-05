package vn.com.be_crm.application.invoice.dto;

// một dòng dữ liệu Invoice từ file import
public record ImportInvoiceRowCommand(
        String code,
        Long orderId,
        String invoiceDate,
        String dueDate,
        String status,
        String paymentStatus,
        String note,
        Long ownerId,
        String ownerEmail
) {}
