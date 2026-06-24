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
    private boolean isRead;
    private LocalDateTime createdAt;
}
