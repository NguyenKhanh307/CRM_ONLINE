package vn.com.be_crm.application.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi tạo mới hoặc cập nhật tồn kho. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpsertInventoryStockCommand {
    private Long id;
    @NotNull(message = "productId không được để trống") private Long productId;
    @NotNull(message = "warehouseId không được để trống") private Long warehouseId;
    @NotNull(message = "quantity không được để trống") private BigDecimal quantity;
    private BigDecimal reservedQuantity;
}
