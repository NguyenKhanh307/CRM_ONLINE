package vn.com.be_crm.presentation.activity.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.domain.activity.enums.ActivityType;

import java.time.LocalDateTime;

/**
 * JSON input khi cập nhật hoạt động.
 */
@Getter
@NoArgsConstructor
public class UpdateActivityRequest {
    private ActivityType type;
    @Size(max = 30)
    private String subject;
    @Size(max = 255)
    private String content;
    private Long assignedUserId;
    private ActivityStatus status;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
}
