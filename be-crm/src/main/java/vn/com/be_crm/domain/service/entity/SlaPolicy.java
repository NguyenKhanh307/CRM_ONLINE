package vn.com.be_crm.domain.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.TicketPriority;

import java.time.LocalDateTime;

/**
 * Domain entity chính sách SLA theo độ ưu tiên (sla_policies).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SlaPolicy {
    /** ID chính sách. */
    private Long id;
    /** Mã chính sách. */
    private String code;
    /** Tên chính sách. */
    private String name;
    /** Độ ưu tiên áp dụng. */
    private TicketPriority priority;
    /** Hạn phản hồi đầu tiên (giờ). */
    private int firstResponseHours;
    /** Hạn giải quyết (giờ). */
    private int resolutionHours;
    /** Còn hiệu lực. */
    private boolean isActive;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật. */
    private LocalDateTime updatedAt;
}
