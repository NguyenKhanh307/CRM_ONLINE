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
    /** ID bản ghi tồn kho. */
    private Long id;
    /** ID sản phẩm. */
    private Long productId;
    /** ID kho. */
    private Long warehouseId;
    /** Số lượng tồn (cho phép âm). */
    private BigDecimal quantity;
    /** Số lượng kiểm kê thực tế (nhập tay). */
    private BigDecimal countedQuantity;
    /** Computed: counted_quantity - quantity — chỉ đọc từ DB. */
    private BigDecimal differenceQuantity;
    /** Ghi chú. */
    private String note;
    /** ID người cập nhật tồn/kiểm kê. */
    private Long updatedBy;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
