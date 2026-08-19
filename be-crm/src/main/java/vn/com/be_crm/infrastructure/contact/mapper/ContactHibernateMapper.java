package vn.com.be_crm.infrastructure.contact.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.core.audit.AuditStamper;
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
        h.setSalutation(d.getSalutation());
        h.setFullName(d.getFullName()); h.setTitle(d.getTitle()); h.setDepartment(d.getDepartment());
        h.setEmail(d.getEmail());
        h.setZalo(d.getZalo()); h.setPhone(d.getPhone()); h.setSource(d.getSource());
        h.setGender(d.getGender()); h.setDateOfBirth(d.getDateOfBirth());
        h.setIsPrimary(d.getIsPrimary() != null ? d.getIsPrimary() : false);
        h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // Đóng dấu người tạo/người sửa (AuditStamper: cần cho body response của PUT)
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Contact toDomain(ContactHibernate h) {
        return Contact.builder()
                .id(h.getId()).customerId(h.getCustomerId()).assignedUserId(h.getAssignedUserId())
                .salutation(h.getSalutation())
                .fullName(h.getFullName()).title(h.getTitle()).department(h.getDepartment())
                .email(h.getEmail())
                .zalo(h.getZalo()).phone(h.getPhone()).source(h.getSource())
                .gender(h.getGender()).dateOfBirth(h.getDateOfBirth())
                .isPrimary(h.getIsPrimary()).createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt())
                .deletedAt(h.getDeletedAt()).deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
