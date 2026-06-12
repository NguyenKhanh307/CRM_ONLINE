package vn.com.be_crm.domain.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Domain entity đại diện cho tồn kho + kiểm kê thực tế. differenceQuantity là computed (DB). */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryStock {
    private Long id;
    private Long productId;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal countedQuantity;
    /** Computed: counted_quantity - quantity — chỉ đọc từ DB. */
    private BigDecimal differenceQuantity;
    private String note;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
