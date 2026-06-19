package vn.com.be_crm.domain.opportunity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Domain entity đại diện cho dòng sản phẩm trong cơ hội.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityItem {
    /** ID dòng sản phẩm. */
    private Long id;
    /** ID cơ hội. */
    private Long opportunityId;
    /** ID sản phẩm. */
    private Long productId;
    /** Số lượng. */
    private BigDecimal quantity;
    /** Đơn giá. */
    private BigDecimal unitPrice;
    /** Chiết khấu. */
    private BigDecimal discount;
    /** Thành tiền. */
    private BigDecimal amount;
    /** Ghi chú. */
    private String note;
}
