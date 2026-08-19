package vn.com.be_crm.infrastructure.service.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.service.entity.SlaPolicy;
import vn.com.be_crm.infrastructure.service.entity.SlaPolicyHibernate;

/** Chuyển đổi giữa SlaPolicy domain entity ↔ SlaPolicyHibernate. */
@Component
public class SlaPolicyHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d domain @return hibernate */
    public SlaPolicyHibernate toHibernate(SlaPolicy d) {
        SlaPolicyHibernate h = new SlaPolicyHibernate();
        h.setId(d.getId()); h.setCode(d.getCode()); h.setName(d.getName()); h.setPriority(d.getPriority());
        h.setFirstResponseHours(d.getFirstResponseHours()); h.setResolutionHours(d.getResolutionHours());
        h.setActive(d.isActive());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public SlaPolicy toDomain(SlaPolicyHibernate h) {
        return SlaPolicy.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).priority(h.getPriority())
                .firstResponseHours(h.getFirstResponseHours()).resolutionHours(h.getResolutionHours())
                .isActive(h.isActive()).createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt())
                .build();
    }
}
