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
        h.setId(d.getId()); h.setCode(d.getCode()); h.setCustomerId(d.getCustomerId());
        h.setContactId(d.getContactId()); h.setOwnerId(d.getOwnerId());
        h.setQuoteDate(d.getQuoteDate()); h.setValidUntil(d.getValidUntil());
        h.setStatus(d.getStatus() != null ? d.getStatus() : QuotationStatus.draft);
        h.setSubtotal(d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTax(d.getTax() != null ? d.getTax() : BigDecimal.ZERO);
        h.setTotal(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
        h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public Quotation toDomain(QuotationHibernate h) {
        return Quotation.builder()
                .id(h.getId()).code(h.getCode()).customerId(h.getCustomerId()).contactId(h.getContactId())
                .ownerId(h.getOwnerId()).quoteDate(h.getQuoteDate()).validUntil(h.getValidUntil())
                .status(h.getStatus()).subtotal(h.getSubtotal()).discount(h.getDiscount())
                .tax(h.getTax()).total(h.getTotal()).note(h.getNote())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt()).build();
    }
}
