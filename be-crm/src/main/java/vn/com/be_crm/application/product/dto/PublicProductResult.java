package vn.com.be_crm.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// output công khai cho landing page (/api/tracking/products) — KHÔNG chứa costPrice hay field
// nội bộ nào khác của ProductResult, tránh lộ giá vốn ra ngoài cho khách/đối thủ
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PublicProductResult {
    private Long id;
    private String sku;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String unit;
    private BigDecimal basePrice;
    private BigDecimal vatRate;
    private String description;
}
