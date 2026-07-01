package vn.com.be_crm.application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.ResolutionType;
import vn.com.be_crm.domain.service.enums.ReturnReason;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.enums.TicketType;

import java.time.LocalDateTime;

/** Output DTO cho Ticket. */
@Getter @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
public class TicketResult {
    private Long id;
    private String code;
    private TicketType type;
    private String subject;
    private String description;
    private Long customerId;
    private Long contactId;
    private Long invoiceId;
    private Long productId;
    private TicketChannel channel;
    private TicketPriority priority;
    private TicketStatus status;
    private ReturnReason reason;
    private ResolutionType resolutionType;
    private String resolutionNote;
    private Long assignedUserId;
    private Long slaPolicyId;
    private LocalDateTime firstResponseAt;
    private LocalDateTime slaDueAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private Integer satisfactionScore;
    private String satisfactionComment;
    /** Suy ra: quá hạn SLA khi slaDueAt đã qua mà phiếu chưa resolved/closed. */
    private boolean isOverdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
