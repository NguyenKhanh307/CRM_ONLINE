package vn.com.be_crm.infrastructure.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Hibernate entity ánh xạ bảng inventory_stock. differenceQuantity là computed column. */
@Entity @Table(name = "inventory_stock") @Getter @Setter @NoArgsConstructor
public class InventoryStockHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "product_id", nullable = false) private Long productId;
    @Column(name = "warehouse_id", nullable = false) private Long warehouseId;
    @Column(name = "quantity", nullable = false, precision = 18, scale = 3) private BigDecimal quantity;
    @Column(name = "counted_quantity", precision = 18, scale = 3) private BigDecimal countedQuantity;
    /** Computed column — chỉ đọc, không ghi. */
    @Formula("(counted_quantity - quantity)")
    private BigDecimal differenceQuantity;
    @Column(name = "note", length = 255) private String note;
    @Column(name = "updated_by") private Long updatedBy;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
}
