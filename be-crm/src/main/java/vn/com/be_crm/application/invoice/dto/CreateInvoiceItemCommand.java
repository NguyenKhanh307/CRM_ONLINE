package vn.com.be_crm.application.invoice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới dòng đơn hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateInvoiceItemCommand {
    /** ID đơn hàng — controller set từ path; bỏ trống khi tạo nested kèm đơn hàng. */
    private Long invoiceId;
    private Long productId;
    @Size(max = 20) private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    @Size(max = 255) private String note;
}
