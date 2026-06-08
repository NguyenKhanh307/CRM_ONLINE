package vn.com.be_crm.application.pricing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi tạo mới loại sản phẩm trong chính sách giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreatePricePolicyProductTypeCommand {
    @NotNull private Long pricePolicyId;
    @NotNull private Long productTypeId;
}
