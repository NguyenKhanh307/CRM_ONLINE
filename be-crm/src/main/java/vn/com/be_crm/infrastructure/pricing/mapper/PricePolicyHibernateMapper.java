package vn.com.be_crm.infrastructure.pricing.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyHibernate;

/** Chuyển đổi giữa PricePolicy domain entity ↔ PricePolicyHibernate. */
@Component
public class PricePolicyHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public PricePolicyHibernate toHibernate(PricePolicy d) {
        PricePolicyHibernate h = new PricePolicyHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName()); h.setType(d.getType());
        h.setPriority(d.getPriority() != null ? d.getPriority() : 0);
        h.setStartDate(d.getStartDate()); h.setEndDate(d.getEndDate());
        h.setStatus(d.getStatus() != null ? d.getStatus() : PricePolicyStatus.active);
        h.setCreatedBy(d.getCreatedBy());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public PricePolicy toDomain(PricePolicyHibernate h) {
        return PricePolicy.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).type(h.getType()).priority(h.getPriority())
                .startDate(h.getStartDate()).endDate(h.getEndDate()).status(h.getStatus())
                .createdBy(h.getCreatedBy()).createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).build();
    }
}
