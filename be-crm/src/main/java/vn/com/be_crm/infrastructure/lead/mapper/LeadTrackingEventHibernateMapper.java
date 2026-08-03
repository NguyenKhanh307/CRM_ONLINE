package vn.com.be_crm.infrastructure.lead.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.infrastructure.lead.entity.LeadTrackingEventHibernate;

// chuyển đổi giữa LeadTrackingEvent domain entity <-> LeadTrackingEventHibernate
@Component
public class LeadTrackingEventHibernateMapper {

    public LeadTrackingEventHibernate toHibernate(LeadTrackingEvent d) {
        LeadTrackingEventHibernate h = new LeadTrackingEventHibernate();
        h.setId(d.getId()); h.setLeadId(d.getLeadId());
        h.setAction(d.getAction()); h.setLabel(d.getLabel());
        h.setPoints(d.getPoints() != null ? d.getPoints() : 0);
        return h;
    }

    public LeadTrackingEvent toDomain(LeadTrackingEventHibernate h) {
        return LeadTrackingEvent.builder()
                .id(h.getId()).leadId(h.getLeadId())
                .action(h.getAction()).label(h.getLabel())
                .points(h.getPoints()).createdAt(h.getCreatedAt()).build();
    }
}
