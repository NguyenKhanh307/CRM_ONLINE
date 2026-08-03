package vn.com.be_crm.infrastructure.customer.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;
import vn.com.be_crm.infrastructure.customer.entity.CustomerHibernate;
import vn.com.be_crm.core.audit.AuditStamper;

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
        h.setShortName(d.getShortName());
        h.setType(d.getType() != null ? d.getType() : CustomerType.company);
        h.setTaxCode(d.getTaxCode()); h.setPhone(d.getPhone()); h.setEmail(d.getEmail());
        h.setWebsite(d.getWebsite()); h.setAddress(d.getAddress());
        h.setIndustry(d.getIndustry()); h.setSource(d.getSource());
        h.setStatus(d.getStatus() != null ? d.getStatus() : CustomerStatus.active);
        h.setCreditDays(d.getCreditDays()); h.setCreditLimit(d.getCreditLimit());
        h.setBankAccount(d.getBankAccount()); h.setBankName(d.getBankName());
        h.setRating(d.getRating()); h.setAnnualRevenue(d.getAnnualRevenue());
        h.setEmployeeSize(d.getEmployeeSize()); h.setDistributor(d.isDistributor());
        h.setOwnerId(d.getOwnerId());
        h.setDeletedAt(d.getDeletedAt());
        h.setDeletedBy(d.getDeletedBy()); h.setPurged(d.isPurged());
        // Đóng dấu người tạo/người sửa (xem AuditStamper: cần cho body response của PUT)
        return AuditStamper.stamp(h, d.getCreatedBy(), d.getUpdatedBy());
    }

    /**
     * Chuyển Hibernate entity sang domain entity.
     * @param h hibernate entity @return domain entity
     */
    public Customer toDomain(CustomerHibernate h) {
        return Customer.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).shortName(h.getShortName())
                .type(h.getType())
                .taxCode(h.getTaxCode()).phone(h.getPhone()).email(h.getEmail())
                .website(h.getWebsite()).address(h.getAddress())
                .industry(h.getIndustry()).source(h.getSource()).status(h.getStatus())
                .creditDays(h.getCreditDays()).creditLimit(h.getCreditLimit())
                .bankAccount(h.getBankAccount()).bankName(h.getBankName())
                .rating(h.getRating()).annualRevenue(h.getAnnualRevenue())
                .employeeSize(h.getEmployeeSize()).isDistributor(h.isDistributor())
                .ownerId(h.getOwnerId())
                .createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).deletedAt(h.getDeletedAt())
                .deletedBy(h.getDeletedBy()).isPurged(h.isPurged()).build();
    }
}
