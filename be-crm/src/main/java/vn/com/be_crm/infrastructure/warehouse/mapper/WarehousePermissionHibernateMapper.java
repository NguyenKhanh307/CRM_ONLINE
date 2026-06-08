package vn.com.be_crm.infrastructure.warehouse.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.warehouse.entity.WarehousePermission;
import vn.com.be_crm.domain.warehouse.enums.WarehousePermissionType;
import vn.com.be_crm.infrastructure.warehouse.entity.WarehousePermissionHibernate;

/** Chuyển đổi WarehousePermission ↔ WarehousePermissionHibernate. */
@Component
public class WarehousePermissionHibernateMapper {
    /** @param d domain @return hibernate */
    public WarehousePermissionHibernate toHibernate(WarehousePermission d) {
        WarehousePermissionHibernate h = new WarehousePermissionHibernate();
        h.setId(d.getId()); h.setWarehouseId(d.getWarehouseId()); h.setUserId(d.getUserId());
        h.setPermission(d.getPermission() != null ? d.getPermission() : WarehousePermissionType.view); return h;
    }
    /** @param h hibernate @return domain */
    public WarehousePermission toDomain(WarehousePermissionHibernate h) {
        return WarehousePermission.builder().id(h.getId()).warehouseId(h.getWarehouseId())
                .userId(h.getUserId()).permission(h.getPermission()).createdAt(h.getCreatedAt()).build();
    }
}
