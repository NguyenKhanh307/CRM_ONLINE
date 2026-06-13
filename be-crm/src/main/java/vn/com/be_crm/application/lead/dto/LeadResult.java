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
    private String companyName;
    private String leadType;
    private Long ownerId;
    private Long customerId;
    private Long contactId;
    private String title;
    private String department;
    private String taxCode;
    private String website;
    private String industry;
    private String source;
    private LeadStatus status;
    private BigDecimal estimatedValue;
    private String phone;
    private String email;
    private boolean doNotCall;
    private boolean doNotEmail;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
