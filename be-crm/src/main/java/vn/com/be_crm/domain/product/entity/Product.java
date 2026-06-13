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
    /** Đơn vị phụ (vd mét/cây). */
    private String secondaryUnit;
    /** 1 đơn vị chính = ? đơn vị phụ. */
    private BigDecimal conversionRate;
    /** Thành phần (vd 100% Polyester). */
    private String composition;
    /** Chỉ số sợi (vd 30/1, 75/36/1). */
    private String yarnCount;
    private String color;
    /** Khổ vải (cm). */
    private BigDecimal fabricWidth;
    /** Định lượng vải (g/m2). */
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
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
