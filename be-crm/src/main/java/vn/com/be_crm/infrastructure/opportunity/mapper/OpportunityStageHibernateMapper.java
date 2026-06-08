package vn.com.be_crm.infrastructure.opportunity.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;
import vn.com.be_crm.infrastructure.opportunity.entity.OpportunityStageHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa OpportunityStage domain entity ↔ OpportunityStageHibernate. */
@Component
public class OpportunityStageHibernateMapper {
    /** @param d domain @return hibernate */
    public OpportunityStageHibernate toHibernate(OpportunityStage d) {
        OpportunityStageHibernate h = new OpportunityStageHibernate();
        h.setId(d.getId()); h.setName(d.getName());
        h.setSortOrder(d.getSortOrder() != null ? d.getSortOrder() : 0);
        h.setProbability(d.getProbability() != null ? d.getProbability() : BigDecimal.ZERO);
        h.setIsWon(d.getIsWon() != null ? d.getIsWon() : false);
        h.setIsLost(d.getIsLost() != null ? d.getIsLost() : false);
        return h;
    }
    /** @param h hibernate @return domain */
    public OpportunityStage toDomain(OpportunityStageHibernate h) {
        return OpportunityStage.builder().id(h.getId()).name(h.getName())
                .sortOrder(h.getSortOrder()).probability(h.getProbability())
                .isWon(h.getIsWon()).isLost(h.getIsLost())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).build();
    }
}
