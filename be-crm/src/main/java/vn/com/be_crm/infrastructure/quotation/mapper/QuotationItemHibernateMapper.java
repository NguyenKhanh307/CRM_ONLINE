package vn.com.be_crm.infrastructure.quotation.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.enums.QuotationLineStatus;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationItemHibernate;

import java.math.BigDecimal;

// chuyển đổi giữa QuotationItem domain entity <-> QuotationItemHibernate
@Component
public class QuotationItemHibernateMapper {

    public QuotationItemHibernate toHibernate(QuotationItem d) {
        QuotationItemHibernate h = new QuotationItemHibernate();
        h.setId(d.getId()); h.setQuotationId(d.getQuotationId()); h.setProductId(d.getProductId());
        h.setOpportunityItemId(d.getOpportunityItemId());
        h.setUnit(d.getUnit());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ONE);
        h.setUnitPrice(d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setTaxRate(d.getTaxRate() != null ? d.getTaxRate() : BigDecimal.ZERO);
        h.setLineStatus(d.getLineStatus() != null ? d.getLineStatus() : QuotationLineStatus.pending);
        h.setNote(d.getNote());
        return h;
    }

    public QuotationItem toDomain(QuotationItemHibernate h) {
        return QuotationItem.builder()
                .id(h.getId()).quotationId(h.getQuotationId()).productId(h.getProductId())
                .opportunityItemId(h.getOpportunityItemId())
                .unit(h.getUnit())
                .quantity(h.getQuantity()).unitPrice(h.getUnitPrice()).discount(h.getDiscount())
                .taxRate(h.getTaxRate()).lineStatus(h.getLineStatus()).note(h.getNote()).build();
    }
}
