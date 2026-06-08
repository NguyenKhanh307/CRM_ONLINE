package vn.com.be_crm.application.activity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.domain.activity.enums.ActivityType;

import java.time.LocalDateTime;

/**
 * Input DTO khi tạo mới hoạt động.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityCommand {

    @NotNull(message = "Loại hoạt động không được để trống")
    private ActivityType type;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 30)
    private String subject;

    @Size(max = 255)
    private String content;

    private String targetType;
    private Long targetId;
    private Long assignedUserId;
    private ActivityStatus status;
    private LocalDateTime dueAt;
}
