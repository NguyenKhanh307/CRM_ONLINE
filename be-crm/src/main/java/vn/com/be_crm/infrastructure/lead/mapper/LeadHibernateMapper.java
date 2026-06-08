package vn.com.be_crm.infrastructure.lead.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.infrastructure.lead.entity.LeadHibernate;

/** Chuyển đổi giữa Lead domain entity ↔ LeadHibernate. */
@Component
public class LeadHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public LeadHibernate toHibernate(Lead d) {
        LeadHibernate h = new LeadHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName());
        h.setOwnerId(d.getOwnerId()); h.setCustomerId(d.getCustomerId()); h.setContactId(d.getContactId());
        h.setSource(d.getSource());
        h.setStatus(d.getStatus() != null ? d.getStatus() : LeadStatus.new_);
        h.setEstimatedValue(d.getEstimatedValue()); h.setPhone(d.getPhone());
        h.setEmail(d.getEmail()); h.setNote(d.getNote()); h.setDeletedAt(d.getDeletedAt());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Lead toDomain(LeadHibernate h) {
        return Lead.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).ownerId(h.getOwnerId())
                .customerId(h.getCustomerId()).contactId(h.getContactId()).source(h.getSource())
                .status(h.getStatus()).estimatedValue(h.getEstimatedValue()).phone(h.getPhone())
                .email(h.getEmail()).note(h.getNote()).createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt()).build();
    }
}
