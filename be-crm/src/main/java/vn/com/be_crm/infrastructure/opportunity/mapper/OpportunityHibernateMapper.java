package vn.com.be_crm.infrastructure.opportunity.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.infrastructure.opportunity.entity.OpportunityHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa Opportunity domain entity ↔ OpportunityHibernate. */
@Component
public class OpportunityHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public OpportunityHibernate toHibernate(Opportunity d) {
        OpportunityHibernate h = new OpportunityHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName());
        h.setCustomerId(d.getCustomerId()); h.setContactId(d.getContactId());
        h.setOwnerId(d.getOwnerId()); h.setStageId(d.getStageId());
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setProbability(d.getProbability()); h.setExpectedCloseDate(d.getExpectedCloseDate());
        h.setStatus(d.getStatus() != null ? d.getStatus() : OpportunityStatus.open);
        h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Opportunity toDomain(OpportunityHibernate h) {
        return Opportunity.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).customerId(h.getCustomerId())
                .contactId(h.getContactId()).ownerId(h.getOwnerId()).stageId(h.getStageId())
                .amount(h.getAmount()).probability(h.getProbability())
                .expectedCloseDate(h.getExpectedCloseDate()).status(h.getStatus())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
