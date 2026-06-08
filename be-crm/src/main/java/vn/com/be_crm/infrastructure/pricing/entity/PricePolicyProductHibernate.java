package vn.com.be_crm.infrastructure.pricing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.pricing.enums.DiscountType;

import java.math.BigDecimal;

/**
 * Hibernate entity ánh xạ bảng price_policy_products.
 */
@Entity
@Table(name = "price_policy_products")
@Getter @Setter @NoArgsConstructor
public class PricePolicyProductHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "price_policy_id", nullable = false) private Long pricePolicyId;
    @Column(name = "product_id", nullable = false) private Long productId;
    @Column(name = "price", precision = 18, scale = 2) private BigDecimal price;
    @Enumerated(EnumType.STRING) @Column(name = "discount_type", length = 10) private DiscountType discountType;
    @Column(name = "discount_value", precision = 18, scale = 2) private BigDecimal discountValue;
    @Column(name = "min_qty", precision = 18, scale = 3) private BigDecimal minQty;
}
