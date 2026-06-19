package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadActivityType;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho hoạt động trong tiềm năng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadActivity {
    /** ID hoạt động. */
    private Long id;
    /** ID tiềm năng. */
    private Long leadId;
    /** Loại hoạt động (gọi/email/hẹn gặp...). */
    private LeadActivityType type;
    /** Tiêu đề. */
    private String subject;
    /** Nội dung. */
    private String content;
    /** Thời điểm đến hạn. */
    private LocalDateTime dueAt;
    /** Thời điểm hoàn thành. */
    private LocalDateTime completedAt;
    /** ID người tạo. */
    private Long createdBy;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
