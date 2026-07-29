package vn.com.be_crm.application.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi tạo mới danh mục hàng hóa. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateProductCategoryCommand {
    @NotBlank(message = "Mã danh mục không được để trống") @Size(max = 20) private String code;
    @NotBlank(message = "Tên danh mục không được để trống") @Size(max = 40) private String name;
    private Integer sortOrder;
    private Boolean isActive;
}
