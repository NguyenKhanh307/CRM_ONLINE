package vn.com.be_crm.application.opportunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi tạo mới cơ hội bán hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOpportunityCommand {
    @NotBlank(message = "Mã cơ hội không được để trống") @Size(max = 20) private String code;
    @NotBlank(message = "Tên cơ hội không được để trống") @Size(max = 40) private String name;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long stageId;
    private BigDecimal amount;
    private BigDecimal probability;
    private LocalDate expectedCloseDate;
    private OpportunityStatus status;
}
