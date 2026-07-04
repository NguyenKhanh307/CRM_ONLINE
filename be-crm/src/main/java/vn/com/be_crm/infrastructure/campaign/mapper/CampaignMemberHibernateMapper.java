package vn.com.be_crm.infrastructure.campaign.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.campaign.entity.CampaignMember;
import vn.com.be_crm.domain.campaign.enums.CampaignMemberStatus;
import vn.com.be_crm.infrastructure.campaign.entity.CampaignMemberHibernate;

/** Chuyển đổi giữa CampaignMember domain entity ↔ CampaignMemberHibernate. */
@Component
public class CampaignMemberHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public CampaignMemberHibernate toHibernate(CampaignMember d) {
        CampaignMemberHibernate h = new CampaignMemberHibernate();
        h.setId(d.getId()); h.setCampaignId(d.getCampaignId());
        h.setLeadId(d.getLeadId()); h.setContactId(d.getContactId());
        h.setName(d.getName()); h.setEmail(d.getEmail()); h.setPhone(d.getPhone());
        h.setStatus(d.getStatus() != null ? d.getStatus() : CampaignMemberStatus.pending);
        h.setSentAt(d.getSentAt()); h.setRespondedAt(d.getRespondedAt());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public CampaignMember toDomain(CampaignMemberHibernate h) {
        return CampaignMember.builder()
                .id(h.getId()).campaignId(h.getCampaignId()).leadId(h.getLeadId()).contactId(h.getContactId())
                .name(h.getName()).email(h.getEmail()).phone(h.getPhone()).status(h.getStatus())
                .sentAt(h.getSentAt()).respondedAt(h.getRespondedAt()).createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt()).build();
    }
}
