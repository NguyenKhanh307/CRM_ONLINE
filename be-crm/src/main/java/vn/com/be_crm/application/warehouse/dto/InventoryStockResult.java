package vn.com.be_crm.application.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Output DTO của InventoryStock UseCase. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryStockResult {
    private Long id;
    private Long productId;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal countedQuantity;
    private BigDecimal differenceQuantity;
    private String note;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
