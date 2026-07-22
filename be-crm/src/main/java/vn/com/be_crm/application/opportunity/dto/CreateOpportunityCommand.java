package vn.com.be_crm.application.opportunity.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Input DTO khi tạo mới cơ hội bán hàng (kèm dòng hàng nếu có). */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOpportunityCommand {
    @NotBlank(message = "Mã cơ hội không được để trống") @Size(max = 20) private String code;
    @NotBlank(message = "Tên cơ hội không được để trống") @Size(max = 40) private String name;
    @Size(max = 20) private String opportunityType;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long stageId;
    private Long pricePolicyId;
    private BigDecimal amount;
    @PositiveOrZero(message = "Doanh thu kỳ vọng không được âm") private BigDecimal expectedRevenue;
    // probability: KHÔNG nhận từ FE — suy ra từ opportunity_stages.probability của giai đoạn được chọn.
    private LocalDate expectedCloseDate;
    @Size(max = 30) private String source;
    private Long campaignId;
    @Size(max = 255) private String winLossReason;
    @Size(max = 500) private String description;
    private OpportunityStatus status;
    /** Dòng hàng tạo kèm cơ hội (opportunityId bỏ trống). */
    @Valid private List<CreateOpportunityItemCommand> items;
}
