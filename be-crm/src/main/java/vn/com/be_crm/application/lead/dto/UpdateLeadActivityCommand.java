package vn.com.be_crm.application.lead.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadActivityType;

import java.time.LocalDateTime;

/** Input DTO khi cập nhật hoạt động tiềm năng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateLeadActivityCommand {
    private Long id;
    private LeadActivityType type;
    @Size(max = 30) private String subject;
    @Size(max = 255) private String content;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
}
