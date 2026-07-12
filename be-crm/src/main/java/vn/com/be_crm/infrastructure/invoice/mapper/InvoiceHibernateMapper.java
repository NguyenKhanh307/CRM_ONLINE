package vn.com.be_crm.infrastructure.invoice.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.infrastructure.shared.audit.AuditStamper;
import vn.com.be_crm.infrastructure.invoice.entity.InvoiceHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa Invoice domain entity ↔ InvoiceHibernate. */
@Component
public class InvoiceHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public InvoiceHibernate toHibernate(Invoice d) {
        InvoiceHibernate h = new InvoiceHibernate();
        // Khóa chính & các liên kết (khách hàng, liên hệ, báo giá, cơ hội)
        h.setId(d.getId()); h.setCode(d.getCode()); h.setCustomerId(d.getCustomerId());
        h.setContactId(d.getContactId());
        h.setQuotationId(d.getQuotationId()); h.setOpportunityId(d.getOpportunityId());
        h.setOrderId(d.getOrderId()); h.setCampaignId(d.getCampaignId());
        h.setOwnerId(d.getOwnerId());
        // Giá trị mặc định khi null (tiền tệ=VND, tỷ giá=1, trạng thái=draft/unpaid)
        h.setInvoiceDate(d.getInvoiceDate());
        h.setDueDate(d.getDueDate());
        h.setCurrency(d.getCurrency() != null ? d.getCurrency() : "VND");
        h.setExchangeRate(d.getExchangeRate() != null ? d.getExchangeRate() : BigDecimal.ONE);
        h.setStatus(d.getStatus() != null ? d.getStatus() : InvoiceStatus.draft);
        h.setPaymentStatus(d.getPaymentStatus() != null ? d.getPaymentStatus() : PaymentStatus.unpaid);
        h.setLocked(d.isLocked());
        h.setBillingAddress(d.getBillingAddress());
        h.setTaxCode(d.getTaxCode());
        // Các trường tiền: mặc định 0 nếu null để tránh NPE khi tính toán/lưu DB
        h.setSubtotal(d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTax(d.getTax() != null ? d.getTax() : BigDecimal.ZERO);
        h.setTotal(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
        // Ghi chú & cờ xóa mềm
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // Đóng dấu người tạo/người sửa (AuditStamper: cần cho body response của PUT)
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public Invoice toDomain(InvoiceHibernate h) {
        return Invoice.builder()
                .id(h.getId()).code(h.getCode()).customerId(h.getCustomerId()).contactId(h.getContactId())
                .quotationId(h.getQuotationId()).opportunityId(h.getOpportunityId())
                .orderId(h.getOrderId()).campaignId(h.getCampaignId())
                .ownerId(h.getOwnerId()).invoiceDate(h.getInvoiceDate()).dueDate(h.getDueDate())
                .currency(h.getCurrency()).exchangeRate(h.getExchangeRate())
                .status(h.getStatus()).paymentStatus(h.getPaymentStatus())
                .isLocked(h.isLocked()).billingAddress(h.getBillingAddress()).taxCode(h.getTaxCode())
                .subtotal(h.getSubtotal())
                .discount(h.getDiscount()).tax(h.getTax()).total(h.getTotal()).note(h.getNote())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
