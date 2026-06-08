package vn.com.be_crm.infrastructure.auth.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.auth.entity.Role;
import vn.com.be_crm.infrastructure.auth.entity.RoleHibernate;

/**
 * Chuyển đổi giữa Role domain entity ↔ RoleHibernate.
 */
@Component
public class RoleHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity để persist.
     *
     * @param domain Role domain entity
     * @return RoleHibernate sẵn sàng lưu DB
     */
    public RoleHibernate toHibernate(Role domain) {
        RoleHibernate h = new RoleHibernate();
        h.setId(domain.getId());
        h.setCode(domain.getCode());
        h.setName(domain.getName());
        h.setDescription(domain.getDescription());
        h.setIsSystem(domain.getIsSystem() != null ? domain.getIsSystem() : false);
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity sau khi đọc từ DB.
     *
     * @param h RoleHibernate đọc từ DB
     * @return Role domain entity
     */
    public Role toDomain(RoleHibernate h) {
        return Role.builder()
                .id(h.getId())
                .code(h.getCode())
                .name(h.getName())
                .description(h.getDescription())
                .isSystem(h.getIsSystem())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt())
                .build();
    }
}
