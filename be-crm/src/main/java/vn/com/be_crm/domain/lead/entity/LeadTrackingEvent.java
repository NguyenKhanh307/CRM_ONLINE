package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// một sự kiện web tracking của tiềm năng (khách bấm nút / nộp form)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadTrackingEvent {
    private Long id;
    private Long leadId;
    // vd view_pricing, download_brochure, submit_form
    private String action;
    private String label;
    private Integer points;
    private LocalDateTime createdAt;
}
