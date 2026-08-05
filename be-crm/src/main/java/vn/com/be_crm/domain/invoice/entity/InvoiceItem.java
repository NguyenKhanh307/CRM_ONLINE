package vn.com.be_crm.domain.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// dòng sản phẩm trong hóa đơn. "amount" không còn là cột lưu sẵn — tính từ quantity/unitPrice/
// discount/taxRate tại thời điểm đọc (xem LineItemTotals).
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {
    private Long id;
    private Long invoiceId;
    private Long productId;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private String note;
}
