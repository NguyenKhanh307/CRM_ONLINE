package vn.com.be_crm.domain.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.domain.activity.enums.ActivityType;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho hoạt động chăm sóc (polymorphic — gắn với mọi loại đối tượng).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    /** ID hoạt động. */
    private Long id;

    /** Loại hoạt động. */
    private ActivityType type;

    /** Tiêu đề hoạt động. */
    private String subject;

    /** Nội dung chi tiết. */
    private String content;

    /** Mức ưu tiên (low/medium/high). */
    private String priority;

    /** Loại đối tượng đích (vd: customer, lead, opportunity). */
    private String targetType;

    /** ID đối tượng đích. */
    private Long targetId;

    /** Loại đối tượng liên quan phụ. */
    private String relatedType;

    /** ID đối tượng liên quan phụ. */
    private Long relatedId;

    /** Địa điểm (lịch hẹn). */
    private String location;

    /** Hướng cuộc gọi (in/out). */
    private String callDirection;

    /** Kết quả cuộc gọi. */
    private String callResult;

    /** Thời lượng cuộc gọi (giây). */
    private Integer callDuration;

    /** ID người dùng được giao. */
    private Long assignedUserId;

    /** Trạng thái hoạt động. */
    private ActivityStatus status;

    /** Thời hạn hoàn thành. */
    private LocalDateTime dueAt;

    /** Thời điểm hoàn thành thực tế. */
    private LocalDateTime completedAt;

    /** Thời điểm tạo. */
    private LocalDateTime createdAt;

    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
