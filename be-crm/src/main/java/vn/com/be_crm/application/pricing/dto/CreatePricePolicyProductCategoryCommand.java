package vn.com.be_crm.application.pricing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi thêm danh mục sản phẩm vào chính sách giá — chọn danh mục thì BE tự bulk-seed
 * toàn bộ sản phẩm thuộc danh mục vào {@code price_policy_products} (giá để trống).
 */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreatePricePolicyProductCategoryCommand {
    private Long pricePolicyId;
    @NotNull private Long categoryId;
}
