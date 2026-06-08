package vn.com.be_crm.infrastructure.auth.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.auth.entity.RolePermission;
import vn.com.be_crm.infrastructure.auth.entity.RolePermissionHibernate;

/**
 * Chuyển đổi giữa RolePermission domain entity ↔ RolePermissionHibernate.
 */
@Component
public class RolePermissionHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity để persist.
     *
     * @param domain RolePermission domain entity
     * @return RolePermissionHibernate sẵn sàng lưu DB
     */
    public RolePermissionHibernate toHibernate(RolePermission domain) {
        RolePermissionHibernate h = new RolePermissionHibernate();
        h.setId(domain.getId());
        h.setRoleId(domain.getRoleId());
        h.setPermissionId(domain.getPermissionId());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity sau khi đọc từ DB.
     *
     * @param h RolePermissionHibernate đọc từ DB
     * @return RolePermission domain entity
     */
    public RolePermission toDomain(RolePermissionHibernate h) {
        return RolePermission.builder()
                .id(h.getId())
                .roleId(h.getRoleId())
                .permissionId(h.getPermissionId())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
