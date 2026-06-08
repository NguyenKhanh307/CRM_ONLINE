package vn.com.be_crm.infrastructure.pricing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hibernate entity ánh xạ bảng price_policy_product_types.
 */
@Entity
@Table(name = "price_policy_product_types")
@Getter @Setter @NoArgsConstructor
public class PricePolicyProductTypeHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "price_policy_id", nullable = false) private Long pricePolicyId;
    @Column(name = "product_type_id", nullable = false) private Long productTypeId;
}
