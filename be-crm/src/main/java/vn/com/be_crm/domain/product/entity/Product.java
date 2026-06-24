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
    /** Giá bán cơ bản. */
    private BigDecimal basePrice;
    /** Giá vốn. */
    private BigDecimal costPrice;
    /** Thuế suất VAT (%). */
    private BigDecimal vatRate;
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
