package vn.com.be_crm.infrastructure.notification.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.notification.entity.Notification;
import vn.com.be_crm.infrastructure.notification.entity.NotificationHibernate;

/** Chuyển đổi giữa Notification domain entity ↔ NotificationHibernate. */
@Component
public class NotificationHibernateMapper {

    /** Chuyển domain entity sang Hibernate entity. @param d domain entity @return hibernate entity */
    public NotificationHibernate toHibernate(Notification d) {
        NotificationHibernate h = new NotificationHibernate();
        h.setId(d.getId()); h.setRecipientUserId(d.getRecipientUserId());
        h.setType(d.getType()); h.setTitle(d.getTitle()); h.setContent(d.getContent());
        h.setLeadId(d.getLeadId()); h.setTargetId(d.getTargetId()); h.setRead(d.isRead());
        return h;
    }

    /** Chuyển Hibernate entity sang domain entity. @param h hibernate entity @return domain entity */
    public Notification toDomain(NotificationHibernate h) {
        return Notification.builder()
                .id(h.getId()).recipientUserId(h.getRecipientUserId())
                .type(h.getType()).title(h.getTitle()).content(h.getContent())
                .leadId(h.getLeadId()).targetId(h.getTargetId()).isRead(h.isRead()).createdAt(h.getCreatedAt()).build();
    }
}
