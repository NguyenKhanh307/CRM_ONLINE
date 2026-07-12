package vn.com.be_crm.application.campaign.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.campaign.enums.CampaignType;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi tạo mới chiến dịch. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateCampaignCommand {
    @NotBlank(message = "Mã chiến dịch không được để trống") @Size(max = 20) private String code;
    @NotBlank(message = "Tên chiến dịch không được để trống") @Size(max = 150) private String name;
    private CampaignType type;
    @Size(max = 50) private String channel;
    @FutureOrPresent(message = "Ngày bắt đầu không được là ngày quá khứ") private LocalDate startDate;
    @FutureOrPresent(message = "Ngày kết thúc không được là ngày quá khứ") private LocalDate endDate;
    private BigDecimal budget;
    private BigDecimal actualCost;
    private Integer targetSize;
    private BigDecimal expectedRevenue;
    private Long ownerId;
    @Size(max = 500) private String description;
}
