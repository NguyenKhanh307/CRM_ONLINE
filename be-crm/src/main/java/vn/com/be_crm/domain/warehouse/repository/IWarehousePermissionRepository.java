package vn.com.be_crm.domain.warehouse.repository;

import vn.com.be_crm.domain.warehouse.entity.WarehousePermission;

import java.util.List;

/** Port lưu trữ cho WarehousePermission (N:N). */
public interface IWarehousePermissionRepository {
    WarehousePermission save(WarehousePermission p);
    void deleteByWarehouseIdAndUserId(Long warehouseId, Long userId);
    List<WarehousePermission> findByWarehouseId(Long warehouseId);
}
