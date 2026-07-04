package vn.com.be_crm.application.invoice.mapper;

import vn.com.be_crm.application.invoice.dto.*;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ Invoice ↔ InvoiceResult. */
public class InvoiceCommandMapper {

    /**
     * Tạo Invoice từ CreateInvoiceCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Invoice toEntity(CreateInvoiceCommand cmd) {
        return Invoice.builder()
                .code(cmd.getCode()).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                .quotationId(cmd.getQuotationId()).opportunityId(cmd.getOpportunityId())
                .orderId(cmd.getOrderId()).campaignId(cmd.getCampaignId())
                .ownerId(cmd.getOwnerId())
                .invoiceDate(cmd.getInvoiceDate()).dueDate(cmd.getDueDate())
                .currency(cmd.getCurrency() != null ? cmd.getCurrency() : "VND")
                .exchangeRate(cmd.getExchangeRate() != null ? cmd.getExchangeRate() : BigDecimal.ONE)
                .status(InvoiceStatus.draft)
                .paymentStatus(PaymentStatus.unpaid)
                .billingAddress(cmd.getBillingAddress()).taxCode(cmd.getTaxCode())
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .tax(cmd.getTax() != null ? cmd.getTax() : BigDecimal.ZERO)
                .total(cmd.getTotal() != null ? cmd.getTotal() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật Invoice từ UpdateInvoiceCommand. Trạng thái & cờ khóa giữ nguyên (đổi qua hành động).
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Invoice toEntity(UpdateInvoiceCommand cmd, Invoice e) {
        return Invoice.builder()
                .id(e.getId()).code(e.getCode())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .quotationId(cmd.getQuotationId() != null ? cmd.getQuotationId() : e.getQuotationId())
                .opportunityId(cmd.getOpportunityId() != null ? cmd.getOpportunityId() : e.getOpportunityId())
                .orderId(cmd.getOrderId() != null ? cmd.getOrderId() : e.getOrderId())
                .campaignId(cmd.getCampaignId() != null ? cmd.getCampaignId() : e.getCampaignId())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .invoiceDate(cmd.getInvoiceDate() != null ? cmd.getInvoiceDate() : e.getInvoiceDate())
                .dueDate(cmd.getDueDate() != null ? cmd.getDueDate() : e.getDueDate())
                .currency(cmd.getCurrency() != null ? cmd.getCurrency() : e.getCurrency())
                .exchangeRate(cmd.getExchangeRate() != null ? cmd.getExchangeRate() : e.getExchangeRate())
                .status(e.getStatus())
                .paymentStatus(e.getPaymentStatus())
                .isLocked(e.isLocked())
                .billingAddress(cmd.getBillingAddress() != null ? cmd.getBillingAddress() : e.getBillingAddress())
                .taxCode(cmd.getTaxCode() != null ? cmd.getTaxCode() : e.getTaxCode())
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : e.getSubtotal())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .tax(cmd.getTax() != null ? cmd.getTax() : e.getTax())
                .total(cmd.getTotal() != null ? cmd.getTotal() : e.getTotal())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Invoice sang InvoiceResult.
     * @param e domain entity @return result DTO
     */
    public static InvoiceResult toResult(Invoice e) {
        return InvoiceResult.builder()
                .id(e.getId()).code(e.getCode()).customerId(e.getCustomerId()).contactId(e.getContactId())
                .quotationId(e.getQuotationId()).opportunityId(e.getOpportunityId())
                .orderId(e.getOrderId()).campaignId(e.getCampaignId())
                .ownerId(e.getOwnerId()).invoiceDate(e.getInvoiceDate()).dueDate(e.getDueDate())
                .currency(e.getCurrency()).exchangeRate(e.getExchangeRate())
                .status(e.getStatus()).paymentStatus(e.getPaymentStatus())
                .isLocked(e.isLocked()).billingAddress(e.getBillingAddress()).taxCode(e.getTaxCode())
                .subtotal(e.getSubtotal())
                .discount(e.getDiscount()).tax(e.getTax()).total(e.getTotal()).note(e.getNote())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private InvoiceCommandMapper() {}
}
