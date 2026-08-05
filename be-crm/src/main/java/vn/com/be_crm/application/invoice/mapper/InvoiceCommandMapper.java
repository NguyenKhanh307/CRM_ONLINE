package vn.com.be_crm.application.invoice.mapper;

import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;

// chuyển đổi Command <-> Invoice <-> InvoiceResult. customerId/contactId/quotationId/
// opportunityId/campaignId/currency/exchangeRate/billingAddress/taxCode/subtotal/discount/tax/
// total không còn trên Invoice — mọi liên kết tra qua orderId, tổng tiền tính từ dòng hàng.
public class InvoiceCommandMapper {

    public static Invoice toEntity(CreateInvoiceCommand cmd) {
        return Invoice.builder()
                .code(cmd.getCode()).orderId(cmd.getOrderId())
                .ownerId(cmd.getOwnerId())
                .invoiceDate(cmd.getInvoiceDate()).dueDate(cmd.getDueDate())
                .status(InvoiceStatus.draft)
                .paymentStatus(PaymentStatus.unpaid)
                .note(cmd.getNote()).build();
    }

    // trạng thái & cờ khóa giữ nguyên (đổi qua hành động)
    public static Invoice toEntity(UpdateInvoiceCommand cmd, Invoice e) {
        return Invoice.builder()
                .id(e.getId()).code(e.getCode())
                .orderId(cmd.getOrderId() != null ? cmd.getOrderId() : e.getOrderId())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .invoiceDate(cmd.getInvoiceDate() != null ? cmd.getInvoiceDate() : e.getInvoiceDate())
                .dueDate(cmd.getDueDate() != null ? cmd.getDueDate() : e.getDueDate())
                .status(e.getStatus())
                .paymentStatus(e.getPaymentStatus())
                .isLocked(e.isLocked())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    public static InvoiceResult toResult(Invoice e) {
        return InvoiceResult.builder()
                .id(e.getId()).code(e.getCode())
                .orderId(e.getOrderId())
                .ownerId(e.getOwnerId()).invoiceDate(e.getInvoiceDate()).dueDate(e.getDueDate())
                .status(e.getStatus()).paymentStatus(e.getPaymentStatus())
                .isLocked(e.isLocked()).note(e.getNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private InvoiceCommandMapper() {}
}
