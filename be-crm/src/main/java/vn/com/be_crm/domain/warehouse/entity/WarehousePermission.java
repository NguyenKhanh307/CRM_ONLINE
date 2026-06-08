package vn.com.be_crm.domain.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.warehouse.enums.WarehousePermissionType;

import java.time.LocalDateTime;

/** Domain entity đại diện cho phân quyền kho (N:N). */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class WarehousePermission {
    private Long id;
    private Long warehouseId;
    private Long userId;
    private WarehousePermissionType permission;
    private LocalDateTime createdAt;
}
