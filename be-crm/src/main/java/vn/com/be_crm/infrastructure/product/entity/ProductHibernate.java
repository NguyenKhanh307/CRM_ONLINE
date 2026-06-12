package vn.com.be_crm.infrastructure.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.product.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng products.
 */
@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor
public class ProductHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Column(name = "category_id")
    private Long categoryId;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ProductType type;
    @Column(name = "unit", length = 20)
    private String unit;
    @Column(name = "secondary_unit", length = 20)
    private String secondaryUnit;
    @Column(name = "conversion_rate", precision = 18, scale = 6)
    private BigDecimal conversionRate;
    @Column(name = "composition", length = 100)
    private String composition;
    @Column(name = "yarn_count", length = 30)
    private String yarnCount;
    @Column(name = "color", length = 50)
    private String color;
    @Column(name = "fabric_width", precision = 8, scale = 2)
    private BigDecimal fabricWidth;
    @Column(name = "weight_gsm", precision = 8, scale = 2)
    private BigDecimal weightGsm;
    @Column(name = "brand", length = 100)
    private String brand;
    @Column(name = "origin", length = 100)
    private String origin;
    @Column(name = "base_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal basePrice;
    @Column(name = "cost_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal costPrice;
    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal vatRate;
    @Column(name = "barcode", length = 100)
    private String barcode;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "is_discontinued", nullable = false)
    private Boolean isDiscontinued;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @Column(name = "created_by") private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
