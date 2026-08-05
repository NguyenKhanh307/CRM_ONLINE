package vn.com.be_crm.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// dòng sản phẩm trong đơn hàng. "amount" không còn là cột lưu sẵn — tính từ quantity/unitPrice/
// discount/taxRate tại thời điểm đọc (xem LineItemTotals).
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private String note;
}
