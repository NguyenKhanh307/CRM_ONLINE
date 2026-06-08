package vn.com.be_crm.application.opportunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới giai đoạn pipeline. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOpportunityStageCommand {
    @NotBlank(message = "Tên giai đoạn không được để trống") @Size(max = 40) private String name;
    private Integer sortOrder;
    private BigDecimal probability;
    private Boolean isWon;
    private Boolean isLost;
}
