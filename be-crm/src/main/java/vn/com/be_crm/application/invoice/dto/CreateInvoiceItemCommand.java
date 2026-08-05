package vn.com.be_crm.application.invoice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// input khi tạo mới dòng hóa đơn
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateInvoiceItemCommand {
    // ID hóa đơn — controller set từ path; bỏ trống khi tạo nested kèm hóa đơn
    private Long invoiceId;
    private Long productId;
    @Size(max = 20) private String unit;
    @Positive(message = "Số lượng phải lớn hơn 0") private BigDecimal quantity;
    @PositiveOrZero(message = "Đơn giá không được âm") private BigDecimal unitPrice;
    @PositiveOrZero(message = "Chiết khấu không được âm") private BigDecimal discount;
    @DecimalMin(value = "0", message = "Thuế suất phải từ 0 đến 100") @DecimalMax(value = "100", message = "Thuế suất phải từ 0 đến 100") private BigDecimal taxRate;
    @Size(max = 255) private String note;
}
