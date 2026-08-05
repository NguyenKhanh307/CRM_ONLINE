package vn.com.be_crm.application.notification.mapper;

import vn.com.be_crm.application.notification.dto.NotificationResult;
import vn.com.be_crm.domain.notification.entity.NotificationFeedItem;

// chuyển NotificationFeedItem (domain, đã gộp sẵn nội dung + trạng thái người nhận) -> NotificationResult
public class NotificationMapper {

    public static NotificationResult toResult(NotificationFeedItem e) {
        return NotificationResult.builder()
                .id(e.id()).type(e.type()).title(e.title()).content(e.content())
                .targetType(e.targetType()).targetId(e.targetId())
                .isRead(e.isRead()).createdAt(e.createdAt()).build();
    }

    private NotificationMapper() {}
}
