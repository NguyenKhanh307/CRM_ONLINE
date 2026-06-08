package vn.com.be_crm.infrastructure.auth.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.auth.entity.OrgUnit;
import vn.com.be_crm.infrastructure.auth.entity.OrgUnitHibernate;

/**
 * Chuyển đổi giữa OrgUnit domain entity ↔ OrgUnitHibernate.
 */
@Component
public class OrgUnitHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity để persist.
     *
     * @param domain OrgUnit domain entity
     * @return OrgUnitHibernate sẵn sàng lưu DB
     */
    public OrgUnitHibernate toHibernate(OrgUnit domain) {
        OrgUnitHibernate h = new OrgUnitHibernate();
        h.setId(domain.getId());
        h.setCode(domain.getCode());
        h.setName(domain.getName());
        h.setParentId(domain.getParentId());
        h.setLevel(domain.getLevel() != null ? domain.getLevel() : 1);
        h.setPath(domain.getPath());
        h.setSortOrder(domain.getSortOrder() != null ? domain.getSortOrder() : 0);
        h.setIsActive(domain.getIsActive() != null ? domain.getIsActive() : true);
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity sau khi đọc từ DB.
     *
     * @param h OrgUnitHibernate đọc từ DB
     * @return OrgUnit domain entity
     */
    public OrgUnit toDomain(OrgUnitHibernate h) {
        return OrgUnit.builder()
                .id(h.getId())
                .code(h.getCode())
                .name(h.getName())
                .parentId(h.getParentId())
                .level(h.getLevel())
                .path(h.getPath())
                .sortOrder(h.getSortOrder())
                .isActive(h.getIsActive())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt())
                .build();
    }
}
