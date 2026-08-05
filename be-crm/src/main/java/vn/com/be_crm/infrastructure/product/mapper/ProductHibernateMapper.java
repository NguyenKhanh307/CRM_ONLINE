package vn.com.be_crm.infrastructure.product.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.enums.ProductStatus;
import vn.com.be_crm.domain.product.enums.ProductType;
import vn.com.be_crm.core.audit.AuditStamper;
import vn.com.be_crm.infrastructure.product.entity.ProductHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa Product domain entity ↔ ProductHibernate. */
@Component
public class ProductHibernateMapper {
    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public ProductHibernate toHibernate(Product d) {
        ProductHibernate h = new ProductHibernate();
        h.setId(d.getId()); h.setSku(d.getSku()); h.setName(d.getName());
        h.setCategoryId(d.getCategoryId());
        h.setType(d.getType() != null ? d.getType() : ProductType.goods);
        h.setUnit(d.getUnit());
        h.setBasePrice(d.getBasePrice() != null ? d.getBasePrice() : BigDecimal.ZERO);
        h.setCostPrice(d.getCostPrice() != null ? d.getCostPrice() : BigDecimal.ZERO);
        h.setVatRate(d.getVatRate() != null ? d.getVatRate() : BigDecimal.ZERO);
        h.setDescription(d.getDescription());
        h.setStatus(d.getStatus() != null ? d.getStatus() : ProductStatus.active);
        h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // Đóng dấu người tạo/người sửa (AuditStamper: cần cho body response của PUT)
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }
    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Product toDomain(ProductHibernate h) {
        return Product.builder()
                .id(h.getId()).sku(h.getSku()).name(h.getName()).categoryId(h.getCategoryId())
                .type(h.getType()).unit(h.getUnit())
                .basePrice(h.getBasePrice())
                .costPrice(h.getCostPrice()).vatRate(h.getVatRate())
                .description(h.getDescription()).status(h.getStatus())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
