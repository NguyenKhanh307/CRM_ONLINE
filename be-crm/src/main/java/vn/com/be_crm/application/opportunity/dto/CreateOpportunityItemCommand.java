package vn.com.be_crm.application.opportunity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới dòng sản phẩm trong cơ hội. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOpportunityItemCommand {
    @NotNull private Long opportunityId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal amount;
    @Size(max = 255) private String note;
}
