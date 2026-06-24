package vn.com.be_crm.application.notification.mapper;

import vn.com.be_crm.application.notification.dto.NotificationResult;
import vn.com.be_crm.domain.notification.entity.Notification;

/** Chuyển Notification domain → NotificationResult. */
public class NotificationMapper {

    /** @param e domain entity @return result DTO */
    public static NotificationResult toResult(Notification e) {
        return NotificationResult.builder()
                .id(e.getId()).type(e.getType()).title(e.getTitle()).content(e.getContent())
                .leadId(e.getLeadId()).isRead(e.isRead()).createdAt(e.getCreatedAt()).build();
    }

    private NotificationMapper() {}
}
