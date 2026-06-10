package vn.com.be_crm.infrastructure.auth.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.enums.UserStatus;
import vn.com.be_crm.infrastructure.auth.entity.UserHibernate;

/**
 * Chuyển đổi giữa User domain entity ↔ UserHibernate.
 */
@Component
public class UserHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity để persist.
     *
     * @param domain User domain entity
     * @return UserHibernate sẵn sàng lưu DB
     */
    public UserHibernate toHibernate(User domain) {
        UserHibernate h = new UserHibernate();
        h.setId(domain.getId());
        h.setEmail(domain.getEmail());
        h.setPasswordHash(domain.getPasswordHash());
        h.setFullName(domain.getFullName());
        h.setPhone(domain.getPhone());
        h.setAvatarUrl(domain.getAvatarUrl());
        h.setUnitId(domain.getUnitId());
        h.setStatus(domain.getStatus() != null ? domain.getStatus() : UserStatus.active);
        h.setLastLoginAt(domain.getLastLoginAt());
        h.setDeletedAt(domain.getDeletedAt());
        h.setActivationToken(domain.getActivationToken());
        h.setActivationExpiresAt(domain.getActivationExpiresAt());
        h.setDataAccessFromYear(domain.getDataAccessFromYear());
        return h;
    }

    /**
     * Chuyển Hibernate entity sang domain entity sau khi đọc từ DB.
     *
     * @param h UserHibernate đọc từ DB
     * @return User domain entity
     */
    public User toDomain(UserHibernate h) {
        return User.builder()
                .id(h.getId())
                .email(h.getEmail())
                .passwordHash(h.getPasswordHash())
                .fullName(h.getFullName())
                .phone(h.getPhone())
                .avatarUrl(h.getAvatarUrl())
                .unitId(h.getUnitId())
                .status(h.getStatus())
                .lastLoginAt(h.getLastLoginAt())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt())
                .deletedAt(h.getDeletedAt())
                .activationToken(h.getActivationToken())
                .activationExpiresAt(h.getActivationExpiresAt())
                .dataAccessFromYear(h.getDataAccessFromYear())
                .build();
    }
}
