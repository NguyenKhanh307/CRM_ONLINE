package vn.com.be_crm.application.activity.mapper;

import vn.com.be_crm.application.activity.dto.ActivityResult;
import vn.com.be_crm.application.activity.dto.CreateActivityCommand;
import vn.com.be_crm.application.activity.dto.UpdateActivityCommand;
import vn.com.be_crm.domain.activity.entity.Activity;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;

/**
 * Chuyển đổi giữa Command ↔ Activity domain entity ↔ ActivityResult.
 */
public class ActivityCommandMapper {

    /**
     * Tạo Activity domain entity từ CreateActivityCommand.
     *
     * @param cmd command tạo mới
     * @return Activity domain entity chưa có ID
     */
    public static Activity toEntity(CreateActivityCommand cmd) {
        return Activity.builder()
                .type(cmd.getType())
                .subject(cmd.getSubject())
                .content(cmd.getContent())
                .priority(cmd.getPriority())
                .targetType(cmd.getTargetType())
                .targetId(cmd.getTargetId())
                .relatedType(cmd.getRelatedType())
                .relatedId(cmd.getRelatedId())
                .location(cmd.getLocation())
                .callDirection(cmd.getCallDirection())
                .callResult(cmd.getCallResult())
                .callDuration(cmd.getCallDuration())
                .assignedUserId(cmd.getAssignedUserId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : ActivityStatus.planned)
                .dueAt(cmd.getDueAt())
                .build();
    }

    /**
     * Cập nhật Activity domain entity từ UpdateActivityCommand.
     *
     * @param cmd      command cập nhật
     * @param existing entity hiện tại
     * @return Activity đã cập nhật
     */
    public static Activity toEntity(UpdateActivityCommand cmd, Activity existing) {
        return Activity.builder()
                .id(existing.getId())
                .type(cmd.getType() != null ? cmd.getType() : existing.getType())
                .subject(cmd.getSubject() != null ? cmd.getSubject() : existing.getSubject())
                .content(cmd.getContent() != null ? cmd.getContent() : existing.getContent())
                .priority(cmd.getPriority() != null ? cmd.getPriority() : existing.getPriority())
                .targetType(existing.getTargetType())
                .targetId(existing.getTargetId())
                .relatedType(existing.getRelatedType())
                .relatedId(existing.getRelatedId())
                .location(cmd.getLocation() != null ? cmd.getLocation() : existing.getLocation())
                .callDirection(cmd.getCallDirection() != null ? cmd.getCallDirection() : existing.getCallDirection())
                .callResult(cmd.getCallResult() != null ? cmd.getCallResult() : existing.getCallResult())
                .callDuration(cmd.getCallDuration() != null ? cmd.getCallDuration() : existing.getCallDuration())
                .assignedUserId(cmd.getAssignedUserId() != null ? cmd.getAssignedUserId() : existing.getAssignedUserId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : existing.getStatus())
                .dueAt(cmd.getDueAt() != null ? cmd.getDueAt() : existing.getDueAt())
                .completedAt(cmd.getCompletedAt() != null ? cmd.getCompletedAt() : existing.getCompletedAt())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    /**
     * Chuyển Activity domain entity sang ActivityResult.
     *
     * @param entity domain entity
     * @return ActivityResult
     */
    public static ActivityResult toResult(Activity entity) {
        return ActivityResult.builder()
                .id(entity.getId()).type(entity.getType()).subject(entity.getSubject())
                .content(entity.getContent()).priority(entity.getPriority())
                .targetType(entity.getTargetType()).targetId(entity.getTargetId())
                .relatedType(entity.getRelatedType()).relatedId(entity.getRelatedId())
                .location(entity.getLocation()).callDirection(entity.getCallDirection())
                .callResult(entity.getCallResult()).callDuration(entity.getCallDuration())
                .assignedUserId(entity.getAssignedUserId())
                .status(entity.getStatus()).dueAt(entity.getDueAt())
                .completedAt(entity.getCompletedAt()).createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ActivityCommandMapper() {}
}
