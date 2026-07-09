package vn.com.be_crm.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.product.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Output DTO của Product UseCase. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResult {
    private Long id;
    private String sku;
    private String name;
    private Long categoryId;
    private ProductType type;
    private String unit;
    private BigDecimal basePrice;
    private BigDecimal costPrice;
    private BigDecimal vatRate;
    private String description;
    private Boolean isDiscontinued;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver).
    private String categoryName;
}
