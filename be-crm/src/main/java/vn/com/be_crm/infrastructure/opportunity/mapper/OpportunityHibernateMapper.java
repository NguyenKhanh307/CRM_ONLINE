package vn.com.be_crm.infrastructure.opportunity.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.core.audit.AuditStamper;
import vn.com.be_crm.infrastructure.opportunity.entity.OpportunityHibernate;

import java.math.BigDecimal;

// chuyển đổi giữa Opportunity domain entity <-> OpportunityHibernate
@Component
public class OpportunityHibernateMapper {

    public OpportunityHibernate toHibernate(Opportunity d) {
        OpportunityHibernate h = new OpportunityHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName());
        h.setOpportunityType(d.getOpportunityType());
        h.setCustomerId(d.getCustomerId()); h.setContactId(d.getContactId());
        h.setOwnerId(d.getOwnerId()); h.setStageId(d.getStageId());
        h.setPricePolicyId(d.getPricePolicyId());
        h.setAmount(d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO);
        h.setSource(d.getSource()); h.setCampaignId(d.getCampaignId());
        h.setWinLossReason(d.getWinLossReason()); h.setDescription(d.getDescription());
        h.setStatus(d.getStatus() != null ? d.getStatus() : OpportunityStatus.open);
        h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // đóng dấu người tạo/người sửa ngay ở đây — cần cho body response của PUT
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }

    public Opportunity toDomain(OpportunityHibernate h) {
        return Opportunity.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).opportunityType(h.getOpportunityType())
                .customerId(h.getCustomerId())
                .contactId(h.getContactId()).ownerId(h.getOwnerId()).stageId(h.getStageId())
                .pricePolicyId(h.getPricePolicyId())
                .amount(h.getAmount())
                .source(h.getSource()).campaignId(h.getCampaignId()).winLossReason(h.getWinLossReason()).description(h.getDescription())
                .status(h.getStatus())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
