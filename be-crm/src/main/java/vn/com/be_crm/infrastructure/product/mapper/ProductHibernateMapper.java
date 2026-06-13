package vn.com.be_crm.infrastructure.product.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.enums.ProductType;
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
        h.setUnit(d.getUnit()); h.setSecondaryUnit(d.getSecondaryUnit());
        h.setConversionRate(d.getConversionRate()); h.setComposition(d.getComposition());
        h.setYarnCount(d.getYarnCount()); h.setColor(d.getColor());
        h.setFabricWidth(d.getFabricWidth()); h.setWeightGsm(d.getWeightGsm());
        h.setBrand(d.getBrand()); h.setOrigin(d.getOrigin());
        h.setBasePrice(d.getBasePrice() != null ? d.getBasePrice() : BigDecimal.ZERO);
        h.setCostPrice(d.getCostPrice() != null ? d.getCostPrice() : BigDecimal.ZERO);
        h.setVatRate(d.getVatRate() != null ? d.getVatRate() : BigDecimal.ZERO);
        h.setBarcode(d.getBarcode()); h.setDescription(d.getDescription());
        h.setIsDiscontinued(d.getIsDiscontinued() != null ? d.getIsDiscontinued() : false);
        h.setIsActive(d.getIsActive() != null ? d.getIsActive() : true);
        h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        return h;
    }
    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Product toDomain(ProductHibernate h) {
        return Product.builder()
                .id(h.getId()).sku(h.getSku()).name(h.getName()).categoryId(h.getCategoryId())
                .type(h.getType()).unit(h.getUnit()).secondaryUnit(h.getSecondaryUnit())
                .conversionRate(h.getConversionRate()).composition(h.getComposition())
                .yarnCount(h.getYarnCount()).color(h.getColor())
                .fabricWidth(h.getFabricWidth()).weightGsm(h.getWeightGsm())
                .brand(h.getBrand()).origin(h.getOrigin())
                .basePrice(h.getBasePrice())
                .costPrice(h.getCostPrice()).vatRate(h.getVatRate()).barcode(h.getBarcode())
                .description(h.getDescription()).isDiscontinued(h.getIsDiscontinued())
                .isActive(h.getIsActive()).createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
