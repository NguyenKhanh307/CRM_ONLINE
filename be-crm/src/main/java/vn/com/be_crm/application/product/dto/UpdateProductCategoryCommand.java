package vn.com.be_crm.application.product.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi cập nhật danh mục hàng hóa. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateProductCategoryCommand {
    private Long id;
    @Size(max = 40) private String name;
    private Long parentId;
    private Integer sortOrder;
    private Boolean isActive;
}
