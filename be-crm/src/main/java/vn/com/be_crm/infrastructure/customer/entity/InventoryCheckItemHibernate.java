package vn.com.be_crm.infrastructure.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Hibernate entity ánh xạ bảng inventory_check_items.
 */
@Entity
@Table(name = "inventory_check_items")
@Getter @Setter @NoArgsConstructor
public class InventoryCheckItemHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "check_id", nullable = false)
    private Long checkId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "quantity", precision = 18, scale = 3)
    private BigDecimal quantity;
    @Column(name = "note", length = 255)
    private String note;
}
