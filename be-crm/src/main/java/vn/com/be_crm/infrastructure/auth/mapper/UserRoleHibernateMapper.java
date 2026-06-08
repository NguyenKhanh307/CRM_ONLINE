package vn.com.be_crm.infrastructure.auth.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.auth.entity.UserRole;
import vn.com.be_crm.infrastructure.auth.entity.UserRoleHibernate;

/**
 * Chuyển đổi giữa UserRole domain entity ↔ UserRoleHibernate.
 */
@Component
public class UserRoleHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity để persist.
     *
     * @param domain UserRole domain entity
     * @return UserRoleHibernate sẵn sàng lưu DB
     */
    public UserRoleHibernate toHibernate(UserRole domain) {
        UserRoleHibernate h = new UserRoleHibernate();
        h.setId(domain.getId());
        h.setUserId(domain.getUserId());
        h.setRoleId(domain.getRoleId());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity sau khi đọc từ DB.
     *
     * @param h UserRoleHibernate đọc từ DB
     * @return UserRole domain entity
     */
    public UserRole toDomain(UserRoleHibernate h) {
        return UserRole.builder()
                .id(h.getId())
                .userId(h.getUserId())
                .roleId(h.getRoleId())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
