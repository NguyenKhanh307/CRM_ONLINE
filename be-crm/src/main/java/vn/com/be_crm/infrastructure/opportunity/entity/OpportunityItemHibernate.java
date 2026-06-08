package vn.com.be_crm.infrastructure.opportunity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Hibernate entity ánh xạ bảng opportunity_items.
 */
@Entity
@Table(name = "opportunity_items")
@Getter @Setter @NoArgsConstructor
public class OpportunityItemHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "opportunity_id", nullable = false)
    private Long opportunityId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "quantity", precision = 18, scale = 3)
    private BigDecimal quantity;
    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;
    @Column(name = "discount", precision = 18, scale = 2)
    private BigDecimal discount;
    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;
    @Column(name = "note", length = 255)
    private String note;
}
