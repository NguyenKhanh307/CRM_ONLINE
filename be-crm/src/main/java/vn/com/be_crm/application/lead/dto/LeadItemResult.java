package vn.com.be_crm.application.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.lead.enums.LeadItemInterestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// output cho LeadItem
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class LeadItemResult {
    private Long id;
    private Long leadId;
    private Long productId;
    private BigDecimal quantity;
    private LeadItemInterestType interestType;
    private LocalDateTime createdAt;
}
