package vn.com.be_crm.domain.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho danh mục hàng hóa (cây phân cấp).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {
    /** ID danh mục. */
    private Long id;
    /** Mã danh mục. */
    private String code;
    /** Tên danh mục. */
    private String name;
    /** ID danh mục cha (null nếu là gốc). */
    private Long parentId;
    /** Thứ tự sắp xếp. */
    private Integer sortOrder;
    /** Đang hoạt động. */
    private Boolean isActive;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
