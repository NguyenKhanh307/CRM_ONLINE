package vn.com.be_crm.application.campaign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.enums.CampaignType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho Campaign. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CampaignResult {
    private Long id;
    private String code;
    private String name;
    private CampaignType type;
    private CampaignStatus status;
    private String channel;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private BigDecimal actualCost;
    private Integer targetSize;
    private BigDecimal expectedRevenue;
    private Long ownerId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
