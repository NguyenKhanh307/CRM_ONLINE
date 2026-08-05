package vn.com.be_crm.application.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationLineStatus;

import java.math.BigDecimal;

// output cho QuotationItem. "amount" không còn là cột lưu sẵn — mapper tính từ quantity/
// unitPrice/discount/taxRate (LineItemTotals) trước khi trả về.
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class QuotationItemResult {
    private Long id;
    private Long quotationId;
    private Long productId;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal amount;
    private QuotationLineStatus lineStatus;
    private String note;
}
