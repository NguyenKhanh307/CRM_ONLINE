package vn.com.be_crm.application.opportunity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Output DTO của OpportunityStage UseCase. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OpportunityStageResult {
    private Long id;
    private String name;
    private Integer sortOrder;
    private BigDecimal probability;
    private Boolean isWon;
    private Boolean isLost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
