package vn.com.be_crm.application.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Output DTO cho LeadTransfer. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class LeadTransferResult {
    private Long id;
    private Long leadId;
    private Long fromUserId;
    private Long toUserId;
    private String reason;
    private LocalDateTime transferredAt;
}
