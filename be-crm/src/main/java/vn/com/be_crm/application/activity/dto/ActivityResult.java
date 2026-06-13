package vn.com.be_crm.application.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.domain.activity.enums.ActivityType;

import java.time.LocalDateTime;

/**
 * Output DTO của Activity UseCase.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResult {
    private Long id;
    private ActivityType type;
    private String subject;
    private String content;
    private String priority;
    private String targetType;
    private Long targetId;
    private String relatedType;
    private Long relatedId;
    private String location;
    private String callDirection;
    private String callResult;
    private Integer callDuration;
    private Long assignedUserId;
    private ActivityStatus status;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
