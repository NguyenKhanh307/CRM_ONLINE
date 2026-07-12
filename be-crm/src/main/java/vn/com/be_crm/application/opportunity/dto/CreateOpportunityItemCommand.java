package vn.com.be_crm.application.opportunity.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới dòng sản phẩm trong cơ hội. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOpportunityItemCommand {
    /** ID cơ hội — controller set từ path; bỏ trống khi tạo nested kèm cơ hội. */
    private Long opportunityId;
    private Long productId;
    @Positive(message = "Số lượng phải lớn hơn 0") private BigDecimal quantity;
    @PositiveOrZero(message = "Đơn giá không được âm") private BigDecimal unitPrice;
    @PositiveOrZero(message = "Chiết khấu không được âm") private BigDecimal discount;
    private BigDecimal amount;
    @Size(max = 255) private String note;
}
