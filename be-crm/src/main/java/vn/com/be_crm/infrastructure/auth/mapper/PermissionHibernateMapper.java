package vn.com.be_crm.infrastructure.auth.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.auth.entity.Permission;
import vn.com.be_crm.infrastructure.auth.entity.PermissionHibernate;

/**
 * Chuyển đổi giữa Permission domain entity ↔ PermissionHibernate.
 */
@Component
public class PermissionHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity để persist.
     *
     * @param domain Permission domain entity
     * @return PermissionHibernate sẵn sàng lưu DB
     */
    public PermissionHibernate toHibernate(Permission domain) {
        PermissionHibernate h = new PermissionHibernate();
        h.setId(domain.getId());
        h.setCode(domain.getCode());
        h.setName(domain.getName());
        h.setModule(domain.getModule());
        h.setDescription(domain.getDescription());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity sau khi đọc từ DB.
     *
     * @param h PermissionHibernate đọc từ DB
     * @return Permission domain entity
     */
    public Permission toDomain(PermissionHibernate h) {
        return Permission.builder()
                .id(h.getId())
                .code(h.getCode())
                .name(h.getName())
                .module(h.getModule())
                .description(h.getDescription())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt())
                .build();
    }
}
