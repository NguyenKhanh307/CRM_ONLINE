package vn.com.be_crm.application.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Output DTO cho Notification. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationResult {
    private Long id;
    private String type;
    private String title;
    private String content;
    private Long leadId;
    private Long targetId;
    /** Wrapper Boolean (không dùng primitive): Lombok sinh getIsRead() → JSON giữ key "isRead". */
    private Boolean isRead;
    private LocalDateTime createdAt;
}
