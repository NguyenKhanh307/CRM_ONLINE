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
    /** ID sản phẩm. */
    private Long id;
    /** Mã SKU. */
    private String sku;
    /** Tên sản phẩm. */
    private String name;
    /** ID danh mục. */
    private Long categoryId;
    /** Loại (hàng hóa/dịch vụ). */
    private ProductType type;
    /** Đơn vị tính chính. */
    private String unit;
    /** Đơn vị phụ (vd mét/cây). */
    private String secondaryUnit;
    /** 1 đơn vị chính = ? đơn vị phụ. */
    private BigDecimal conversionRate;
    /** Thành phần (vd 100% Polyester). */
    private String composition;
    /** Chỉ số sợi (vd 30/1, 75/36/1). */
    private String yarnCount;
    /** Màu sắc. */
    private String color;
    /** Khổ vải (cm). */
    private BigDecimal fabricWidth;
    /** Định lượng vải (g/m2). */
    private BigDecimal weightGsm;
    /** Thương hiệu. */
    private String brand;
    /** Xuất xứ. */
    private String origin;
    /** Giá bán cơ bản. */
    private BigDecimal basePrice;
    /** Giá vốn. */
    private BigDecimal costPrice;
    /** Thuế suất VAT (%). */
    private BigDecimal vatRate;
    /** Mã vạch. */
    private String barcode;
    /** Mô tả. */
    private String description;
    /** Ngừng theo dõi/kinh doanh. */
    private Boolean isDiscontinued;
    /** Đang hoạt động. */
    private Boolean isActive;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
