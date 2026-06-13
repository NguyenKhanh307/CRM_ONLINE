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
    private Long id;
    private Long quotationId;
    private Long productId;
    /** Đơn vị tính dòng hàng. */
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    private String note;
}
