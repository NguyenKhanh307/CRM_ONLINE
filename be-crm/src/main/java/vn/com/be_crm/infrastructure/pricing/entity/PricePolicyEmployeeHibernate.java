package vn.com.be_crm.infrastructure.pricing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hibernate entity ánh xạ bảng price_policy_employees.
 */
@Entity
@Table(name = "price_policy_employees")
@Getter @Setter @NoArgsConstructor
public class PricePolicyEmployeeHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "price_policy_id", nullable = false) private Long pricePolicyId;
    @Column(name = "user_id") private Long userId;
}
