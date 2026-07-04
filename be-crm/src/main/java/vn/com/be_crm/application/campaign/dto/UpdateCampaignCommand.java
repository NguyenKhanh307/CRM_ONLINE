package vn.com.be_crm.application.campaign.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.campaign.enums.CampaignType;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi cập nhật chiến dịch. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateCampaignCommand {
    private Long id;
    @Size(max = 150) private String name;
    private CampaignType type;
    @Size(max = 50) private String channel;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private BigDecimal actualCost;
    private Integer targetSize;
    private BigDecimal expectedRevenue;
    private Long ownerId;
    @Size(max = 500) private String description;
}
