package vn.com.be_crm.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Domain entity đại diện cho dòng sản phẩm trong đơn hàng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    /** ID dòng sản phẩm. */
    private Long id;
    /** ID đơn hàng. */
    private Long orderId;
    /** ID sản phẩm. */
    private Long productId;
    /** ID kho xuất hàng. */
    private Long warehouseId;
    /** Đơn vị tính dòng hàng. */
    private String unit;
    /** Số lượng. */
    private BigDecimal quantity;
    /** Đơn giá. */
    private BigDecimal unitPrice;
    /** Chiết khấu. */
    private BigDecimal discount;
    /** Thuế suất (%). */
    private BigDecimal taxRate;
    /** Thành tiền. */
    private BigDecimal amount;
    /** Ghi chú. */
    private String note;
}
