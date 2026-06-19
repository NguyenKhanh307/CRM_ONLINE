package vn.com.be_crm.domain.quotation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Domain entity đại diện cho dòng sản phẩm trong báo giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationItem {
    /** ID dòng sản phẩm. */
    private Long id;
    /** ID báo giá. */
    private Long quotationId;
    /** ID sản phẩm. */
    private Long productId;
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
