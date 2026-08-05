package vn.com.be_crm.infrastructure.notification.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.notification.entity.Notification;
import vn.com.be_crm.infrastructure.notification.entity.NotificationHibernate;

// chuyển đổi giữa Notification domain entity <-> NotificationHibernate
@Component
public class NotificationHibernateMapper {

    public NotificationHibernate toHibernate(Notification d) {
        NotificationHibernate h = new NotificationHibernate();
        h.setId(d.getId());
        h.setType(d.getType()); h.setTitle(d.getTitle()); h.setContent(d.getContent());
        h.setTargetType(d.getTargetType()); h.setTargetId(d.getTargetId());
        return h;
    }

    public Notification toDomain(NotificationHibernate h) {
        return Notification.builder()
                .id(h.getId())
                .type(h.getType()).title(h.getTitle()).content(h.getContent())
                .targetType(h.getTargetType()).targetId(h.getTargetId())
                .createdAt(h.getCreatedAt()).build();
    }
}
