package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import vn.com.be_crm.domain.quotation.enums.QuotationLineStatus;

import java.math.BigDecimal;

// input khi cập nhật dòng báo giá
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateQuotationItemCommand {
    private Long id;
    private Long productId;
    @Size(max = 20) private String unit;
    @Positive(message = "Số lượng phải lớn hơn 0") private BigDecimal quantity;
    @PositiveOrZero(message = "Đơn giá không được âm") private BigDecimal unitPrice;
    @PositiveOrZero(message = "Chiết khấu không được âm") private BigDecimal discount;
    @DecimalMin(value = "0", message = "Thuế suất phải từ 0 đến 100") @DecimalMax(value = "100", message = "Thuế suất phải từ 0 đến 100") private BigDecimal taxRate;
    // khách chấp nhận/từ chối riêng dòng này khi phản hồi báo giá
    private QuotationLineStatus lineStatus;
    @Size(max = 255) private String note;
}
