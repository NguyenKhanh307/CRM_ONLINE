package vn.com.be_crm.domain.notification.entity;

import java.time.LocalDateTime;

// một dòng trong hộp thông báo của người dùng — gộp sẵn nội dung Notification + trạng thái
// riêng của người nhận (NotificationRecipient) để application layer không phải tự join.
// "id" ở đây LÀ id của dòng notification_recipients (không phải notifications) vì mọi thao tác
// đánh dấu đã đọc/xóa đều nhắm vào đúng một người nhận, không ảnh hưởng người khác.
public record NotificationFeedItem(
        Long id,
        Long notificationId,
        String type,
        String title,
        String content,
        String targetType,
        Long targetId,
        boolean isRead,
        LocalDateTime createdAt
) {}
