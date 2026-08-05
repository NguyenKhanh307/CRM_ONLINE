package vn.com.be_crm.infrastructure.lead.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.lead.entity.LeadItem;
import vn.com.be_crm.domain.lead.enums.LeadItemInterestType;
import vn.com.be_crm.infrastructure.lead.entity.LeadItemHibernate;

import java.math.BigDecimal;

// chuyển đổi giữa LeadItem domain entity <-> LeadItemHibernate
@Component
public class LeadItemHibernateMapper {

    public LeadItemHibernate toHibernate(LeadItem d) {
        LeadItemHibernate h = new LeadItemHibernate();
        h.setId(d.getId()); h.setLeadId(d.getLeadId()); h.setProductId(d.getProductId());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ONE);
        h.setInterestType(d.getInterestType() != null ? d.getInterestType() : LeadItemInterestType.viewed);
        return h;
    }

    public LeadItem toDomain(LeadItemHibernate h) {
        return LeadItem.builder()
                .id(h.getId()).leadId(h.getLeadId()).productId(h.getProductId())
                .quantity(h.getQuantity()).interestType(h.getInterestType())
                .createdAt(h.getCreatedAt()).build();
    }
}
