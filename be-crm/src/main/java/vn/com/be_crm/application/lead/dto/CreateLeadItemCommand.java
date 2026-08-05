package vn.com.be_crm.application.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// input khi ghi nhận sản phẩm khách quan tâm/yêu cầu báo giá cho một tiềm năng
// leadId lấy từ path (@PathVariable), KHÔNG @NotNull
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateLeadItemCommand {
    private Long leadId;
    private Long productId;
    private BigDecimal quantity;
    // "viewed" (mặc định) hoặc "requested_quote"
    private String interestType;
}
