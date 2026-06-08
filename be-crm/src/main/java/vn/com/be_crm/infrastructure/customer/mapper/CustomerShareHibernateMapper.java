package vn.com.be_crm.infrastructure.customer.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.customer.entity.CustomerShare;
import vn.com.be_crm.domain.customer.enums.CustomerSharePermission;
import vn.com.be_crm.infrastructure.customer.entity.CustomerShareHibernate;

/** Chuyển đổi giữa CustomerShare domain entity ↔ CustomerShareHibernate. */
@Component
public class CustomerShareHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public CustomerShareHibernate toHibernate(CustomerShare d) {
        CustomerShareHibernate h = new CustomerShareHibernate();
        h.setId(d.getId()); h.setCustomerId(d.getCustomerId()); h.setUserId(d.getUserId());
        h.setPermission(d.getPermission() != null ? d.getPermission() : CustomerSharePermission.view);
        h.setSharedBy(d.getSharedBy());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public CustomerShare toDomain(CustomerShareHibernate h) {
        return CustomerShare.builder()
                .id(h.getId()).customerId(h.getCustomerId()).userId(h.getUserId())
                .permission(h.getPermission()).sharedBy(h.getSharedBy())
                .createdAt(h.getCreatedAt()).build();
    }
}
