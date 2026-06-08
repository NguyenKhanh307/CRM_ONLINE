package vn.com.be_crm.application.customer.mapper;

import vn.com.be_crm.application.customer.dto.*;
import vn.com.be_crm.domain.customer.entity.InventoryCheckItem;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ InventoryCheckItem ↔ InventoryCheckItemResult. */
public class InventoryCheckItemCommandMapper {

    /**
     * Tạo InventoryCheckItem từ CreateInventoryCheckItemCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static InventoryCheckItem toEntity(CreateInventoryCheckItemCommand cmd) {
        return InventoryCheckItem.builder()
                .checkId(cmd.getCheckId()).productId(cmd.getProductId())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật InventoryCheckItem từ UpdateInventoryCheckItemCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static InventoryCheckItem toEntity(UpdateInventoryCheckItemCommand cmd, InventoryCheckItem e) {
        return InventoryCheckItem.builder()
                .id(e.getId()).checkId(e.getCheckId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .quantity(cmd.getQuantity() != null ? cmd.getQuantity() : e.getQuantity())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote()).build();
    }

    /**
     * Chuyển InventoryCheckItem sang InventoryCheckItemResult.
     * @param e domain entity @return result DTO
     */
    public static InventoryCheckItemResult toResult(InventoryCheckItem e) {
        return InventoryCheckItemResult.builder()
                .id(e.getId()).checkId(e.getCheckId()).productId(e.getProductId())
                .quantity(e.getQuantity()).note(e.getNote()).build();
    }

    private InventoryCheckItemCommandMapper() {}
}
