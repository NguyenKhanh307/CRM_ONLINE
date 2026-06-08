package vn.com.be_crm.infrastructure.customer.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.customer.entity.InventoryCheckItem;
import vn.com.be_crm.infrastructure.customer.entity.InventoryCheckItemHibernate;

import java.math.BigDecimal;

/** Chuyển đổi giữa InventoryCheckItem domain entity ↔ InventoryCheckItemHibernate. */
@Component
public class InventoryCheckItemHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public InventoryCheckItemHibernate toHibernate(InventoryCheckItem d) {
        InventoryCheckItemHibernate h = new InventoryCheckItemHibernate();
        h.setId(d.getId()); h.setCheckId(d.getCheckId()); h.setProductId(d.getProductId());
        h.setQuantity(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ZERO);
        h.setNote(d.getNote());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public InventoryCheckItem toDomain(InventoryCheckItemHibernate h) {
        return InventoryCheckItem.builder()
                .id(h.getId()).checkId(h.getCheckId()).productId(h.getProductId())
                .quantity(h.getQuantity()).note(h.getNote()).build();
    }
}
