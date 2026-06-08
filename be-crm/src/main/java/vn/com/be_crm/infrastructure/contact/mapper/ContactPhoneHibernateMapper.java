package vn.com.be_crm.infrastructure.contact.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.contact.entity.ContactPhone;
import vn.com.be_crm.domain.contact.enums.PhoneType;
import vn.com.be_crm.infrastructure.contact.entity.ContactPhoneHibernate;

/** Chuyển đổi giữa ContactPhone domain entity ↔ ContactPhoneHibernate. */
@Component
public class ContactPhoneHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public ContactPhoneHibernate toHibernate(ContactPhone d) {
        ContactPhoneHibernate h = new ContactPhoneHibernate();
        h.setId(d.getId()); h.setContactId(d.getContactId()); h.setPhone(d.getPhone());
        h.setPhoneType(d.getPhoneType() != null ? d.getPhoneType() : PhoneType.mobile);
        h.setIsPrimary(d.getIsPrimary() != null ? d.getIsPrimary() : false);
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public ContactPhone toDomain(ContactPhoneHibernate h) {
        return ContactPhone.builder()
                .id(h.getId()).contactId(h.getContactId()).phone(h.getPhone())
                .phoneType(h.getPhoneType()).isPrimary(h.getIsPrimary()).createdAt(h.getCreatedAt()).build();
    }
}
