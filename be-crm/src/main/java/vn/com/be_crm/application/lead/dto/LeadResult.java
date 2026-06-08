package vn.com.be_crm.application.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Output DTO cho Lead. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class LeadResult {
    private Long id;
    private String code;
    private String name;
    private Long ownerId;
    private Long customerId;
    private Long contactId;
    private String source;
    private LeadStatus status;
    private BigDecimal estimatedValue;
    private String phone;
    private String email;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
