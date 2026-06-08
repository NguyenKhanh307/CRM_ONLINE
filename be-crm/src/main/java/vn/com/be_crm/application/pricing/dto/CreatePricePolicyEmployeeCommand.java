package vn.com.be_crm.application.pricing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi tạo mới nhân viên trong chính sách giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreatePricePolicyEmployeeCommand {
    @NotNull private Long pricePolicyId;
    private Long userId;
    private Long unitId;
}
