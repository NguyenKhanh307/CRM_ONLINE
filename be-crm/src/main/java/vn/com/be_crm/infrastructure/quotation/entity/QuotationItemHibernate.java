package vn.com.be_crm.infrastructure.quotation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.quotation.enums.QuotationLineStatus;

import java.math.BigDecimal;

// ánh xạ bảng quotation_items. Không có cột "amount" — thành tiền tính từ quantity/unitPrice/
// discount/taxRate tại thời điểm đọc.
@Entity
@Table(name = "quotation_items")
@Getter @Setter @NoArgsConstructor
public class QuotationItemHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "quotation_id", nullable = false) private Long quotationId;
    @Column(name = "product_id") private Long productId;
    @Column(name = "opportunity_item_id") private Long opportunityItemId;
    @Column(name = "unit", length = 20) private String unit;
    @Column(name = "quantity", precision = 18, scale = 3) private BigDecimal quantity;
    @Column(name = "unit_price", precision = 18, scale = 2) private BigDecimal unitPrice;
    @Column(name = "discount", precision = 18, scale = 2) private BigDecimal discount;
    @Column(name = "tax_rate", precision = 5, scale = 2) private BigDecimal taxRate;
    @Enumerated(EnumType.STRING)
    @Column(name = "line_status", length = 20) private QuotationLineStatus lineStatus;
    @Column(name = "note", length = 255) private String note;
}
