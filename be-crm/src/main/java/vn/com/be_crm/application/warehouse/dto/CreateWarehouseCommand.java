package vn.com.be_crm.application.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi tạo mới kho hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateWarehouseCommand {
    @NotBlank(message = "Mã kho không được để trống") @Size(max = 20) private String code;
    @NotBlank(message = "Tên kho không được để trống") @Size(max = 40) private String name;
    @Size(max = 255) private String address;
    private Long managerId;
    private Boolean isActive;
}
