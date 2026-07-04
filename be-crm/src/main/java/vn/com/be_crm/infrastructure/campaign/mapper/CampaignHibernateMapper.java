package vn.com.be_crm.infrastructure.campaign.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.enums.CampaignType;
import vn.com.be_crm.infrastructure.campaign.entity.CampaignHibernate;

/** Chuyển đổi giữa Campaign domain entity ↔ CampaignHibernate. */
@Component
public class CampaignHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public CampaignHibernate toHibernate(Campaign d) {
        CampaignHibernate h = new CampaignHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName());
        h.setType(d.getType() != null ? d.getType() : CampaignType.other);
        h.setStatus(d.getStatus() != null ? d.getStatus() : CampaignStatus.draft);
        h.setChannel(d.getChannel());
        h.setStartDate(d.getStartDate()); h.setEndDate(d.getEndDate());
        h.setBudget(d.getBudget()); h.setActualCost(d.getActualCost());
        h.setTargetSize(d.getTargetSize()); h.setExpectedRevenue(d.getExpectedRevenue());
        h.setOwnerId(d.getOwnerId()); h.setDescription(d.getDescription());
        h.setDeletedAt(d.getDeletedAt()); h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public Campaign toDomain(CampaignHibernate h) {
        return Campaign.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).type(h.getType()).status(h.getStatus())
                .channel(h.getChannel()).startDate(h.getStartDate()).endDate(h.getEndDate())
                .budget(h.getBudget()).actualCost(h.getActualCost())
                .targetSize(h.getTargetSize()).expectedRevenue(h.getExpectedRevenue())
                .ownerId(h.getOwnerId()).description(h.getDescription())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
