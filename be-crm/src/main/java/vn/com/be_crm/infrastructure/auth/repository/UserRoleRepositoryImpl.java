package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.auth.entity.UserRole;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.infrastructure.auth.mapper.UserRoleHibernateMapper;
import vn.com.be_crm.core.tx.impl.TxSupport;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IUserRoleRepository — mọi truy vấn dùng native SQL (không HQL).
 */
@Repository
public class UserRoleRepositoryImpl implements IUserRoleRepository {

    private final SessionFactory sf;
    private final UserRoleHibernateMapper mapper;

    /**
     * @param sf Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public UserRoleRepositoryImpl(SessionFactory sf, UserRoleHibernateMapper mapper) {
        this.sf = sf;
        this.mapper = mapper;
    }

    /**
     * Gán vai trò cho người dùng.
     *
     * @param userRole entity gán vai trò
     * @return entity sau khi lưu
     */
    @Override
    public UserRole save(UserRole userRole) {
        return TxSupport.write(sf, s -> {
            var merged = s.merge(mapper.toHibernate(userRole));
            return mapper.toDomain(merged);
        });
    }

    /**
     * Thu hồi vai trò khỏi người dùng bằng native SQL DELETE.
     *
     * @param userId ID người dùng
     * @param roleId ID vai trò cần thu hồi
     */
    @Override
    public void deleteByUserIdAndRoleId(Long userId, Long roleId) {
        TxSupport.writeVoid(sf, s -> {
            s.createNativeQuery("DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
                    .setParameter("userId", userId)
                    .setParameter("roleId", roleId)
                    .executeUpdate();
        });
    }

    /**
     * Xóa toàn bộ vai trò hiện có của một người dùng bằng native SQL DELETE.
     *
     * @param userId ID người dùng
     */
    @Override
    public void deleteByUserId(Long userId) {
        TxSupport.writeVoid(sf, s -> {
            s.createNativeQuery("DELETE FROM user_roles WHERE user_id = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();
        });
    }

    /**
     * Lấy toàn bộ liên kết user-role hiện có bằng native SQL.
     *
     * @return danh sách UserRole
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<UserRole> findAll() {
        return TxSupport.read(sf, s -> {
            List<Object[]> rows = s.createNativeQuery(
                            "SELECT id, user_id, role_id, created_at FROM user_roles", Object[].class)
                    .list();
            return rows.stream().map(UserRoleRepositoryImpl::toDomain).collect(Collectors.toList());
        });
    }

    /**
     * Lấy danh sách code vai trò của một người dùng bằng native SQL JOIN.
     *
     * @param userId ID người dùng
     * @return danh sách code vai trò
     */
    @Override
    public List<String> findRoleCodesByUserId(Long userId) {
        return TxSupport.read(sf, s -> {
            String sql = """
                    SELECT r.code FROM roles r
                    JOIN user_roles ur ON ur.role_id = r.id
                    WHERE ur.user_id = :userId
                    """;
            return s.createNativeQuery(sql, String.class)
                    .setParameter("userId", userId)
                    .list();
        });
    }

    /**
     * Lấy tất cả UserRole theo roleId bằng native SQL.
     *
     * @param roleId ID vai trò
     * @return danh sách UserRole
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<UserRole> findByRoleId(Long roleId) {
        return TxSupport.read(sf, s -> {
            List<Object[]> rows = s.createNativeQuery(
                            "SELECT id, user_id, role_id, created_at FROM user_roles WHERE role_id = :roleId", Object[].class)
                    .setParameter("roleId", roleId)
                    .list();
            return rows.stream().map(UserRoleRepositoryImpl::toDomain).collect(Collectors.toList());
        });
    }

    /**
     * Lấy danh sách ID người dùng có vai trò thuộc tập code cho trước bằng native SQL JOIN.
     *
     * @param roleCodes danh sách code vai trò
     * @return danh sách userId không trùng
     */
    @Override
    public List<Long> findUserIdsByRoleCodes(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) return Collections.emptyList();
        return TxSupport.read(sf, s -> {
            String sql = """
                    SELECT DISTINCT ur.user_id FROM user_roles ur
                    JOIN roles r ON r.id = ur.role_id
                    WHERE r.code IN (:codes)
                    """;
            return s.createNativeQuery(sql, Long.class)
                    .setParameter("codes", roleCodes)
                    .list();
        });
    }

    /** Map một dòng native SQL (id, user_id, role_id, created_at) sang domain UserRole. */
    private static UserRole toDomain(Object[] row) {
        return UserRole.builder()
                .id(((Number) row[0]).longValue())
                .userId(((Number) row[1]).longValue())
                .roleId(((Number) row[2]).longValue())
                .createdAt(row[3] instanceof Timestamp ts ? ts.toLocalDateTime() : (LocalDateTime) row[3])
                .build();
    }
}
