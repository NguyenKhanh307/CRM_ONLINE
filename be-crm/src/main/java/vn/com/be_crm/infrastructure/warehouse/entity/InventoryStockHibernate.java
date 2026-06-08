package vn.com.be_crm.infrastructure.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Hibernate entity ánh xạ bảng inventory_stock. available_quantity là computed column. */
@Entity @Table(name = "inventory_stock") @Getter @Setter @NoArgsConstructor
public class InventoryStockHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "product_id", nullable = false) private Long productId;
    @Column(name = "warehouse_id", nullable = false) private Long warehouseId;
    @Column(name = "quantity", nullable = false, precision = 18, scale = 3) private BigDecimal quantity;
    @Column(name = "reserved_quantity", nullable = false, precision = 18, scale = 3) private BigDecimal reservedQuantity;
    /** Computed column — chỉ đọc, không ghi. */
    @Formula("(quantity - reserved_quantity)")
    private BigDecimal availableQuantity;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
}
