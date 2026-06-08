package vn.com.be_crm.infrastructure.opportunity.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.infrastructure.opportunity.entity.OpportunityItemHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa OpportunityItem domain entity ↔ OpportunityItemHibernate. */
@Component
public class OpportunityItemHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public OpportunityItemHibernate toHibernate(OpportunityItem d) {
        OpportunityItemHibernate h = new OpportunityItemHibernate();
        h.setId(d.getId()); h.setOpportunityId(d.getOpportunityId()); h.setProductId(d.getProductId());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ONE);
        h.setUnitPrice(d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO);
        h.setDiscount(d.getDiscount() != null ? d.getDiscount() : BigDecimal.ZERO);
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setNote(d.getNote());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public OpportunityItem toDomain(OpportunityItemHibernate h) {
        return OpportunityItem.builder()
                .id(h.getId()).opportunityId(h.getOpportunityId()).productId(h.getProductId())
                .quantity(h.getQuantity()).unitPrice(h.getUnitPrice()).discount(h.getDiscount())
                .amount(h.getAmount()).note(h.getNote()).build();
    }
}
