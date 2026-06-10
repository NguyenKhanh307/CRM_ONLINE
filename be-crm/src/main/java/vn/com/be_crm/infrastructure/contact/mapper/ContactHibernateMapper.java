package vn.com.be_crm.infrastructure.contact.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.infrastructure.contact.entity.ContactHibernate;

/** Chuyển đổi giữa Contact domain entity ↔ ContactHibernate. */
@Component
public class ContactHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public ContactHibernate toHibernate(Contact d) {
        ContactHibernate h = new ContactHibernate();
        h.setId(d.getId()); h.setCustomerId(d.getCustomerId()); h.setAssignedUserId(d.getAssignedUserId());
        h.setFullName(d.getFullName()); h.setPosition(d.getPosition()); h.setEmail(d.getEmail());
        h.setGender(d.getGender()); h.setDateOfBirth(d.getDateOfBirth()); h.setAddress(d.getAddress());
        h.setIsPrimary(d.getIsPrimary() != null ? d.getIsPrimary() : false);
        h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Contact toDomain(ContactHibernate h) {
        return Contact.builder()
                .id(h.getId()).customerId(h.getCustomerId()).assignedUserId(h.getAssignedUserId())
                .fullName(h.getFullName()).position(h.getPosition()).email(h.getEmail())
                .gender(h.getGender()).dateOfBirth(h.getDateOfBirth()).address(h.getAddress())
                .isPrimary(h.getIsPrimary()).createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt())
                .deletedAt(h.getDeletedAt()).deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
