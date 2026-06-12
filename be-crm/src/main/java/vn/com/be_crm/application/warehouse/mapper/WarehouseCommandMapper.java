package vn.com.be_crm.application.warehouse.mapper;

import vn.com.be_crm.application.warehouse.dto.*;
import vn.com.be_crm.domain.warehouse.entity.InventoryStock;
import vn.com.be_crm.domain.warehouse.entity.Warehouse;


/** Chuyển đổi Command ↔ Warehouse/InventoryStock ↔ Result. */
public class WarehouseCommandMapper {

    /** @param c command @return entity */
    public static Warehouse toEntity(CreateWarehouseCommand c) {
        return Warehouse.builder().code(c.getCode()).name(c.getName()).address(c.getAddress())
                .isActive(c.getIsActive() != null ? c.getIsActive() : true).build();
    }

    /** @param c command @param e existing @return entity */
    public static Warehouse toEntity(UpdateWarehouseCommand c, Warehouse e) {
        return Warehouse.builder().id(e.getId()).code(e.getCode())
                .name(c.getName() != null ? c.getName() : e.getName())
                .address(c.getAddress() != null ? c.getAddress() : e.getAddress())
                .isActive(c.getIsActive() != null ? c.getIsActive() : e.getIsActive())
                .createdAt(e.getCreatedAt()).build();
    }

    /** @param e entity @return result */
    public static WarehouseResult toResult(Warehouse e) {
        return WarehouseResult.builder().id(e.getId()).code(e.getCode()).name(e.getName())
                .address(e.getAddress()).isActive(e.getIsActive())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    /** @param c command @return entity */
    public static InventoryStock toEntity(UpsertInventoryStockCommand c) {
        return InventoryStock.builder().id(c.getId()).productId(c.getProductId()).warehouseId(c.getWarehouseId())
                .quantity(c.getQuantity()).countedQuantity(c.getCountedQuantity())
                .note(c.getNote()).updatedBy(c.getUpdatedBy()).build();
    }

    /** @param e entity @return result */
    public static InventoryStockResult toStockResult(InventoryStock e) {
        return InventoryStockResult.builder().id(e.getId()).productId(e.getProductId())
                .warehouseId(e.getWarehouseId()).quantity(e.getQuantity())
                .countedQuantity(e.getCountedQuantity()).differenceQuantity(e.getDifferenceQuantity())
                .note(e.getNote()).updatedBy(e.getUpdatedBy()).updatedAt(e.getUpdatedAt()).build();
    }

    private WarehouseCommandMapper() {}
}
