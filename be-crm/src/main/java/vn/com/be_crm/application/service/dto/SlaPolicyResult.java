package vn.com.be_crm.application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.TicketPriority;

/** Output DTO cho SlaPolicy. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class SlaPolicyResult {
    private Long id;
    private String code;
    private String name;
    private TicketPriority priority;
    private int firstResponseHours;
    private int resolutionHours;
    private Boolean isActive;
}
