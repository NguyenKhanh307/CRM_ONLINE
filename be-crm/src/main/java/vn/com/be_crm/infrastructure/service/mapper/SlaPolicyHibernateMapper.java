package vn.com.be_crm.infrastructure.service.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.service.entity.SlaPolicy;
import vn.com.be_crm.infrastructure.service.entity.SlaPolicyHibernate;

/** Chuyển đổi giữa SlaPolicy domain entity ↔ SlaPolicyHibernate. */
@Component
public class SlaPolicyHibernateMapper {
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public SlaPolicy toDomain(SlaPolicyHibernate h) {
        return SlaPolicy.builder()
                .id(h.getId()).code(h.getCode()).name(h.getName()).priority(h.getPriority())
                .firstResponseHours(h.getFirstResponseHours()).resolutionHours(h.getResolutionHours())
                .isActive(h.isActive()).createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt())
                .build();
    }
}
