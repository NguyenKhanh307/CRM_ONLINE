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
    private Long id;
    private Long leadId;
    private LeadActivityType type;
    private String subject;
    private String content;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
