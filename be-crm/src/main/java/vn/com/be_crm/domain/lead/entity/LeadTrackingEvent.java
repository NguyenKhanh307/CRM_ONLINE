package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity ghi lại một sự kiện web tracking của tiềm năng (khách bấm nút / nộp form).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadTrackingEvent {
    /** ID sự kiện. */
    private Long id;
    /** ID tiềm năng. */
    private Long leadId;
    /** Mã hành động (vd view_pricing, download_brochure, submit_form). */
    private String action;
    /** Nhãn hiển thị của hành động. */
    private String label;
    /** Số điểm cộng cho hành động này. */
    private Integer points;
    /** Thời điểm phát sinh. */
    private LocalDateTime createdAt;
}
