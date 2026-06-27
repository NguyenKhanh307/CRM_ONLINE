package vn.com.be_crm.application.opportunity.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi cập nhật cơ hội bán hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateOpportunityCommand {
    private Long id;
    @Size(max = 40) private String name;
    @Size(max = 20) private String opportunityType;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long stageId;
    private Long pricePolicyId;
    private BigDecimal amount;
    private BigDecimal expectedRevenue;
    private BigDecimal probability;
    private LocalDate expectedCloseDate;
    @Size(max = 30) private String source;
    @Size(max = 255) private String winLossReason;
    @Size(max = 500) private String description;
    private OpportunityStatus status;
}
