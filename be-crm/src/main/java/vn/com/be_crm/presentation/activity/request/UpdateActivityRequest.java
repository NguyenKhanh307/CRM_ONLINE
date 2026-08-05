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
    @Size(max = 100)
    private String subject;
    @Size(max = 255)
    private String content;
    @Size(max = 10)
    private String priority;
    @Size(max = 255)
    private String location;
    @Size(max = 5)
    private String callDirection;
    @Size(max = 50)
    private String callResult;
    private Integer callDuration;
    private Long assignedUserId;
    private ActivityStatus status;
    private LocalDateTime dueAt;
}
