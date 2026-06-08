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
    private Long id;
    private String code;
    private String name;
    /** ID danh mục cha (null nếu là gốc). */
    private Long parentId;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
