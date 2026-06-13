package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới dòng báo giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateQuotationItemCommand {
    /** ID báo giá — controller set từ path; bỏ trống khi tạo nested kèm báo giá. */
    private Long quotationId;
    private Long productId;
    @Size(max = 20) private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    @Size(max = 255) private String note;
}
