package vn.com.be_crm.infrastructure.lead.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.lead.entity.LeadTransfer;
import vn.com.be_crm.infrastructure.lead.entity.LeadTransferHibernate;

// chuyển đổi giữa LeadTransfer domain entity <-> LeadTransferHibernate
@Component
public class LeadTransferHibernateMapper {

    public LeadTransferHibernate toHibernate(LeadTransfer d) {
        LeadTransferHibernate h = new LeadTransferHibernate();
        h.setId(d.getId()); h.setLeadId(d.getLeadId()); h.setFromUserId(d.getFromUserId());
        h.setToUserId(d.getToUserId()); h.setReason(d.getReason());
        return h;
    }

    public LeadTransfer toDomain(LeadTransferHibernate h) {
        return LeadTransfer.builder()
                .id(h.getId()).leadId(h.getLeadId()).fromUserId(h.getFromUserId())
                .toUserId(h.getToUserId()).reason(h.getReason()).transferredAt(h.getTransferredAt()).build();
    }
}
