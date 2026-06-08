package vn.com.be_crm.application.pricing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi tạo mới danh mục khách hàng trong chính sách giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreatePricePolicyCustomerCategoryCommand {
    @NotNull private Long pricePolicyId;
    @NotNull private Long categoryId;
}
