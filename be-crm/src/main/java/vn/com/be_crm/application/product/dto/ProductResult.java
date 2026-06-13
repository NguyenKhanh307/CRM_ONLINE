package vn.com.be_crm.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.product.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Output DTO của Product UseCase. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResult {
    private Long id;
    private String sku;
    private String name;
    private Long categoryId;
    private ProductType type;
    private String unit;
    private String secondaryUnit;
    private BigDecimal conversionRate;
    private String composition;
    private String yarnCount;
    private String color;
    private BigDecimal fabricWidth;
    private BigDecimal weightGsm;
    private String brand;
    private String origin;
    private BigDecimal basePrice;
    private BigDecimal costPrice;
    private BigDecimal vatRate;
    private String barcode;
    private String description;
    private Boolean isDiscontinued;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
