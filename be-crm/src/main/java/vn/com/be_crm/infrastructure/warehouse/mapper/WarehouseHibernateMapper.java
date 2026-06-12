package vn.com.be_crm.infrastructure.warehouse.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.warehouse.entity.Warehouse;
import vn.com.be_crm.infrastructure.warehouse.entity.WarehouseHibernate;

/** Chuyển đổi Warehouse ↔ WarehouseHibernate. */
@Component
public class WarehouseHibernateMapper {
    /** @param d domain @return hibernate */
    public WarehouseHibernate toHibernate(Warehouse d) {
        WarehouseHibernate h = new WarehouseHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName());
        h.setAddress(d.getAddress());
        h.setIsActive(d.getIsActive() != null ? d.getIsActive() : true); return h;
    }
    /** @param h hibernate @return domain */
    public Warehouse toDomain(WarehouseHibernate h) {
        return Warehouse.builder().id(h.getId()).code(h.getCode()).name(h.getName())
                .address(h.getAddress()).isActive(h.getIsActive())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).build();
    }
}
