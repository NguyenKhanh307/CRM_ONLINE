package vn.com.be_crm.infrastructure.product.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.product.entity.ProductCategory;
import vn.com.be_crm.infrastructure.product.entity.ProductCategoryHibernate;

/** Chuyển đổi giữa ProductCategory domain entity ↔ ProductCategoryHibernate. */
@Component
public class ProductCategoryHibernateMapper {
    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public ProductCategoryHibernate toHibernate(ProductCategory d) {
        ProductCategoryHibernate h = new ProductCategoryHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName());
        h.setSortOrder(d.getSortOrder() != null ? d.getSortOrder() : 0);
        h.setIsActive(d.getIsActive() != null ? d.getIsActive() : true);
        return h;
    }
    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public ProductCategory toDomain(ProductCategoryHibernate h) {
        return ProductCategory.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName())
                .sortOrder(h.getSortOrder()).isActive(h.getIsActive())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).build();
    }
}
