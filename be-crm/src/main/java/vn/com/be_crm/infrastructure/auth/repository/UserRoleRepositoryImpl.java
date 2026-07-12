package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.auth.entity.UserRole;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.infrastructure.auth.entity.UserRoleHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.UserRoleHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Hibernate implementation của IUserRoleRepository.
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
            UserRoleHibernate merged = s.merge(mapper.toHibernate(userRole));
            return mapper.toDomain(merged);
        });
    }

    /**
     * Thu hồi vai trò khỏi người dùng bằng HQL DELETE.
     *
     * @param userId ID người dùng
     * @param roleId ID vai trò cần thu hồi
     */
    @Override
    public void deleteByUserIdAndRoleId(Long userId, Long roleId) {
        TxSupport.writeVoid(sf, s -> {
            s.createMutationQuery(
                    "DELETE FROM UserRoleHibernate WHERE userId = :userId AND roleId = :roleId")
                    .setParameter("userId", userId)
                    .setParameter("roleId", roleId)
                    .executeUpdate();
            });
    }

    /**
     * Lấy danh sách code vai trò của một người dùng bằng HQL subquery.
     *
     * @param userId ID người dùng
     * @return danh sách code vai trò
     */
    @Override
    public List<String> findRoleCodesByUserId(Long userId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery(
                    "SELECT r.code FROM RoleHibernate r WHERE r.id IN " +
                    "(SELECT ur.roleId FROM UserRoleHibernate ur WHERE ur.userId = :userId)",
                    String.class)
                    .setParameter("userId", userId)
                    .list();
        });
    }

    /**
     * Lấy tất cả UserRole theo roleId.
     *
     * @param roleId ID vai trò
     * @return danh sách UserRole
     */
    @Override
    public List<UserRole> findByRoleId(Long roleId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery(
                    "FROM UserRoleHibernate WHERE roleId = :roleId", UserRoleHibernate.class)
                    .setParameter("roleId", roleId)
                    .list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }

    /**
     * Lấy danh sách ID người dùng có vai trò thuộc tập code cho trước.
     *
     * @param roleCodes danh sách code vai trò
     * @return danh sách userId không trùng
     */
    @Override
    public List<Long> findUserIdsByRoleCodes(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) return List.of();
        return TxSupport.read(sf, s -> {
            return s.createQuery(
                    "SELECT DISTINCT ur.userId FROM UserRoleHibernate ur WHERE ur.roleId IN " +
                    "(SELECT r.id FROM RoleHibernate r WHERE r.code IN (:codes))",
                    Long.class)
                    .setParameter("codes", roleCodes)
                    .list();
        });
    }
}
