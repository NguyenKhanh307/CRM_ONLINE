package vn.com.be_crm.infrastructure.lead.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.lead.entity.LeadActivity;
import vn.com.be_crm.infrastructure.lead.entity.LeadActivityHibernate;

/** Chuyển đổi giữa LeadActivity domain entity ↔ LeadActivityHibernate. */
@Component
public class LeadActivityHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public LeadActivityHibernate toHibernate(LeadActivity d) {
        LeadActivityHibernate h = new LeadActivityHibernate();
        h.setId(d.getId()); h.setLeadId(d.getLeadId()); h.setType(d.getType());
        h.setSubject(d.getSubject()); h.setContent(d.getContent());
        h.setDueAt(d.getDueAt()); h.setCompletedAt(d.getCompletedAt()); h.setCreatedBy(d.getCreatedBy());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public LeadActivity toDomain(LeadActivityHibernate h) {
        return LeadActivity.builder()
                .id(h.getId()).leadId(h.getLeadId()).type(h.getType()).subject(h.getSubject())
                .content(h.getContent()).dueAt(h.getDueAt()).completedAt(h.getCompletedAt())
                .createdBy(h.getCreatedBy()).createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).build();
    }
}
