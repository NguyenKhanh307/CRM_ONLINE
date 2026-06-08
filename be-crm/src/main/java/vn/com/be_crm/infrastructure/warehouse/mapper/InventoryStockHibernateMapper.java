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
        h.setReservedQuantity(d.getReservedQuantity() != null ? d.getReservedQuantity() : BigDecimal.ZERO);
        return h;
    }
    /** @param h hibernate @return domain */
    public InventoryStock toDomain(InventoryStockHibernate h) {
        return InventoryStock.builder().id(h.getId()).productId(h.getProductId())
                .warehouseId(h.getWarehouseId()).quantity(h.getQuantity())
                .reservedQuantity(h.getReservedQuantity()).availableQuantity(h.getAvailableQuantity())
                .updatedAt(h.getUpdatedAt()).build();
    }
}
