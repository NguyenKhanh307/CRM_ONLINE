package vn.com.be_crm.infrastructure.warehouse.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.warehouse.entity.InventoryStock;
import vn.com.be_crm.infrastructure.warehouse.entity.InventoryStockHibernate;

import java.math.BigDecimal;

/** Chuyển đổi InventoryStock ↔ InventoryStockHibernate. */
@Component
public class InventoryStockHibernateMapper {
    /** @param d domain @return hibernate */
    public InventoryStockHibernate toHibernate(InventoryStock d) {
        InventoryStockHibernate h = new InventoryStockHibernate();
        h.setId(d.getId()); h.setProductId(d.getProductId()); h.setWarehouseId(d.getWarehouseId());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ZERO);
        h.setCountedQuantity(d.getCountedQuantity());
        h.setNote(d.getNote());
        h.setUpdatedBy(d.getUpdatedBy());
        return h;
    }
    /** @param h hibernate @return domain */
    public InventoryStock toDomain(InventoryStockHibernate h) {
        return InventoryStock.builder().id(h.getId()).productId(h.getProductId())
                .warehouseId(h.getWarehouseId()).quantity(h.getQuantity())
                .countedQuantity(h.getCountedQuantity()).differenceQuantity(h.getDifferenceQuantity())
                .note(h.getNote()).updatedBy(h.getUpdatedBy()).updatedAt(h.getUpdatedAt()).build();
    }
}
