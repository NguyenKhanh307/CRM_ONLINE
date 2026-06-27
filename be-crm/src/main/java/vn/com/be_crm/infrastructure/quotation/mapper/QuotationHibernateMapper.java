package vn.com.be_crm.infrastructure.quotation.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa Quotation domain entity ↔ QuotationHibernate. */
@Component
public class QuotationHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public QuotationHibernate toHibernate(Quotation d) {
        QuotationHibernate h = new QuotationHibernate();
        // Khóa chính & các liên kết (khách hàng, liên hệ, cơ hội)
        h.setId(d.getId()); h.setCode(d.getCode()); h.setCustomerId(d.getCustomerId());
        h.setContactId(d.getContactId()); h.setOpportunityId(d.getOpportunityId());
        h.setPricePolicyId(d.getPricePolicyId());
        h.setPrimary(d.isPrimary()); h.setLocked(d.isLocked());
        h.setOwnerId(d.getOwnerId());
        h.setQuoteDate(d.getQuoteDate()); h.setValidUntil(d.getValidUntil());
        // Giá trị mặc định khi null (tiền tệ=VND, tỷ giá=1, trạng thái=draft)
        h.setCurrency(d.getCurrency() != null ? d.getCurrency() : "VND");
        h.setExchangeRate(d.getExchangeRate() != null ? d.getExchangeRate() : BigDecimal.ONE);
        h.setStatus(d.getStatus() != null ? d.getStatus() : QuotationStatus.draft);
        // Các trường tiền: mặc định 0 nếu null để tránh NPE khi tính toán/lưu DB
        h.setSubtotal(d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTax(d.getTax() != null ? d.getTax() : BigDecimal.ZERO);
        h.setTotal(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
        // Ghi chú & cờ xóa mềm
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public Quotation toDomain(QuotationHibernate h) {
        return Quotation.builder()
                .id(h.getId()).code(h.getCode()).customerId(h.getCustomerId()).contactId(h.getContactId())
                .opportunityId(h.getOpportunityId())
                .pricePolicyId(h.getPricePolicyId()).isPrimary(h.isPrimary()).isLocked(h.isLocked())
                .ownerId(h.getOwnerId()).quoteDate(h.getQuoteDate()).validUntil(h.getValidUntil())
                .currency(h.getCurrency()).exchangeRate(h.getExchangeRate())
                .status(h.getStatus()).subtotal(h.getSubtotal()).discount(h.getDiscount())
                .tax(h.getTax()).total(h.getTotal()).note(h.getNote())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
