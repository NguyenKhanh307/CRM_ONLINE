package vn.com.be_crm.domain.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.product.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho hàng hóa / dịch vụ.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String sku;
    private String name;
    private Long categoryId;
    private ProductType type;
    private String unit;
    private BigDecimal basePrice;
    private BigDecimal costPrice;
    private BigDecimal vatRate;
    private String barcode;
    private String description;
    private Boolean isDiscontinued;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
}
