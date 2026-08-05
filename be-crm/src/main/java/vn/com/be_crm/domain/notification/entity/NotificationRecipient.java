package vn.com.be_crm.domain.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// trạng thái của MỘT người nhận đối với MỘT thông báo (đã đọc chưa, đã xóa khỏi hộp chưa) —
// tách khỏi Notification để một thông báo có thể gửi tới nhiều người mà không lặp lại nội dung
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecipient {
    private Long id;
    private Long notificationId;
    private Long recipientUserId;
    private boolean isRead;
    private LocalDateTime createdAt;
    // thời điểm người nhận xóa mềm thông báo — null nghĩa là còn trong hộp thông báo
    private LocalDateTime deletedAt;
}
