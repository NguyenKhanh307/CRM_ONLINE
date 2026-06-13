package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi cập nhật dòng báo giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateQuotationItemCommand {
    private Long id;
    private Long productId;
    @Size(max = 20) private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    @Size(max = 255) private String note;
}
