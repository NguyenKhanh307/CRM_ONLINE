package vn.com.be_crm.infrastructure.invoice.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.infrastructure.invoice.entity.InvoiceItemHibernate;

import java.math.BigDecimal;

// chuyển đổi giữa InvoiceItem domain entity <-> InvoiceItemHibernate
@Component
public class InvoiceItemHibernateMapper {

    public InvoiceItemHibernate toHibernate(InvoiceItem d) {
        InvoiceItemHibernate h = new InvoiceItemHibernate();
        h.setId(d.getId()); h.setInvoiceId(d.getInvoiceId()); h.setProductId(d.getProductId());
        h.setUnit(d.getUnit());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ONE);
        h.setUnitPrice(d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTaxRate(d.getTaxRate() != null ? d.getTaxRate() : BigDecimal.ZERO);
        h.setNote(d.getNote());
        return h;
    }

    public InvoiceItem toDomain(InvoiceItemHibernate h) {
        return InvoiceItem.builder()
                .id(h.getId()).invoiceId(h.getInvoiceId()).productId(h.getProductId())
                .unit(h.getUnit())
                .quantity(h.getQuantity()).unitPrice(h.getUnitPrice()).discount(h.getDiscount())
                .taxRate(h.getTaxRate()).note(h.getNote()).build();
    }
}
