package vn.com.be_crm.application.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.warehouse.enums.WarehousePermissionType;

/** Input DTO khi gán / thu hồi quyền kho. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignWarehousePermissionCommand {
    @NotNull private Long warehouseId;
    @NotNull private Long userId;
    private WarehousePermissionType permission;
}
