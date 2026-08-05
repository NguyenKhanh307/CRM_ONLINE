package vn.com.be_crm.domain.quotation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import vn.com.be_crm.domain.quotation.enums.QuotationLineStatus;

import java.math.BigDecimal;

// dòng sản phẩm trong báo giá. "amount" không còn là cột lưu sẵn — tính từ quantity/unitPrice/
// discount/taxRate tại thời điểm đọc (xem LineItemTotals).
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuotationItem {
    private Long id;
    private Long quotationId;
    private Long productId;
    // ID dòng cơ hội nguồn (để đồng bộ hai chiều với cơ hội)
    private Long opportunityItemId;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    // khách chấp nhận/từ chối riêng từng dòng khi phản hồi báo giá — mặc định pending
    private QuotationLineStatus lineStatus;
    private String note;
}
