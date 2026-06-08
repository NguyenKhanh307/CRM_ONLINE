package vn.com.be_crm.infrastructure.customer.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;
import vn.com.be_crm.infrastructure.customer.entity.CustomerHibernate;

/** Chuyển đổi giữa Customer domain entity ↔ CustomerHibernate. */
@Component
public class CustomerHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity.
     * @param d domain entity @return hibernate entity
     */
    public CustomerHibernate toHibernate(Customer d) {
        CustomerHibernate h = new CustomerHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName());
        h.setType(d.getType() != null ? d.getType() : CustomerType.company);
        h.setTaxCode(d.getTaxCode()); h.setPhone(d.getPhone()); h.setEmail(d.getEmail());
        h.setAddress(d.getAddress()); h.setSource(d.getSource());
        h.setStatus(d.getStatus() != null ? d.getStatus() : CustomerStatus.active);
        h.setOwnerId(d.getOwnerId()); h.setUnitId(d.getUnitId());
        h.setDeletedAt(d.getDeletedAt());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Customer toDomain(CustomerHibernate h) {
        return Customer.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).type(h.getType())
                .taxCode(h.getTaxCode()).phone(h.getPhone()).email(h.getEmail())
                .address(h.getAddress()).source(h.getSource()).status(h.getStatus())
                .ownerId(h.getOwnerId()).unitId(h.getUnitId())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt()).build();
    }
}
