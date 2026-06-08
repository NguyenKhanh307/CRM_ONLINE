package vn.com.be_crm.infrastructure.customer.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.customer.entity.InventoryCheck;
import vn.com.be_crm.domain.customer.enums.InventoryCheckStatus;
import vn.com.be_crm.infrastructure.customer.entity.InventoryCheckHibernate;

/** Chuyển đổi giữa InventoryCheck domain entity ↔ InventoryCheckHibernate. */
@Component
public class InventoryCheckHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public InventoryCheckHibernate toHibernate(InventoryCheck d) {
        InventoryCheckHibernate h = new InventoryCheckHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setCustomerId(d.getCustomerId());
        h.setCheckedBy(d.getCheckedBy()); h.setCheckDate(d.getCheckDate());
        h.setStatus(d.getStatus() != null ? d.getStatus() : InventoryCheckStatus.draft);
        h.setNote(d.getNote());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public InventoryCheck toDomain(InventoryCheckHibernate h) {
        return InventoryCheck.builder()
                .id(h.getId()).code(h.getCode()).customerId(h.getCustomerId())
                .checkedBy(h.getCheckedBy()).checkDate(h.getCheckDate()).status(h.getStatus())
                .note(h.getNote()).createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).build();
    }
}
