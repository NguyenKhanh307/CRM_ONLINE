package vn.com.be_crm.application.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// output cho Notification. "id" là id dòng notification_recipients — dùng cho mark-read/xóa
// đúng một người nhận.
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationResult {
    private Long id;
    private String type;
    private String title;
    private String content;
    private String targetType;
    private Long targetId;
    // wrapper Boolean (không dùng primitive): Lombok sinh getIsRead() -> JSON giữ key "isRead"
    private Boolean isRead;
    private LocalDateTime createdAt;
}
