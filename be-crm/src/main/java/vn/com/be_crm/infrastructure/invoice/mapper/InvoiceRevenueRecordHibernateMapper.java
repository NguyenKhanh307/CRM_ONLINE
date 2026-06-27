package vn.com.be_crm.infrastructure.invoice.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.invoice.entity.InvoiceRevenueRecord;
import vn.com.be_crm.infrastructure.invoice.entity.InvoiceRevenueRecordHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa InvoiceRevenueRecord domain entity ↔ InvoiceRevenueRecordHibernate. */
@Component
public class InvoiceRevenueRecordHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public InvoiceRevenueRecordHibernate toHibernate(InvoiceRevenueRecord d) {
        InvoiceRevenueRecordHibernate h = new InvoiceRevenueRecordHibernate();
        h.setId(d.getId()); h.setInvoiceId(d.getInvoiceId()); h.setUserId(d.getUserId());
        h.setRevenueAmount(d.getRevenueAmount() != null ? d.getRevenueAmount() : BigDecimal.ZERO);
        h.setPercentage(d.getPercentage() != null ? d.getPercentage() : BigDecimal.ZERO);
        h.setNote(d.getNote());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public InvoiceRevenueRecord toDomain(InvoiceRevenueRecordHibernate h) {
        return InvoiceRevenueRecord.builder()
                .id(h.getId()).invoiceId(h.getInvoiceId()).userId(h.getUserId())
                .revenueAmount(h.getRevenueAmount()).percentage(h.getPercentage())
                .note(h.getNote()).createdAt(h.getCreatedAt()).build();
    }
}
