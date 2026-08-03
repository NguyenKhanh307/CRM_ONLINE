package vn.com.be_crm.infrastructure.opportunity.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.core.audit.AuditStamper;
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
        h.setOpportunityType(d.getOpportunityType());
        h.setCustomerId(d.getCustomerId()); h.setContactId(d.getContactId());
        h.setOwnerId(d.getOwnerId()); h.setStageId(d.getStageId());
        h.setPricePolicyId(d.getPricePolicyId());
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setExpectedRevenue(d.getExpectedRevenue());
        h.setProbability(d.getProbability()); h.setExpectedCloseDate(d.getExpectedCloseDate());
        h.setSource(d.getSource()); h.setCampaignId(d.getCampaignId());
        h.setWinLossReason(d.getWinLossReason()); h.setDescription(d.getDescription());
        h.setStatus(d.getStatus() != null ? d.getStatus() : OpportunityStatus.open);
        h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // Đóng dấu người tạo/người sửa (AuditStamper: cần cho body response của PUT)
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Opportunity toDomain(OpportunityHibernate h) {
        return Opportunity.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).opportunityType(h.getOpportunityType())
                .customerId(h.getCustomerId())
                .contactId(h.getContactId()).ownerId(h.getOwnerId()).stageId(h.getStageId())
                .pricePolicyId(h.getPricePolicyId())
                .amount(h.getAmount()).expectedRevenue(h.getExpectedRevenue()).probability(h.getProbability())
                .expectedCloseDate(h.getExpectedCloseDate())
                .source(h.getSource()).campaignId(h.getCampaignId()).winLossReason(h.getWinLossReason()).description(h.getDescription())
                .status(h.getStatus())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
