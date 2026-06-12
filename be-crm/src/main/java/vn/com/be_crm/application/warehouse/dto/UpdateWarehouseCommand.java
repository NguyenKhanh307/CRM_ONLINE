package vn.com.be_crm.application.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi cập nhật kho hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateWarehouseCommand {
    @NotNull private Long id;
    @Size(max = 40) private String name;
    @Size(max = 255) private String address;
    private Boolean isActive;
}
