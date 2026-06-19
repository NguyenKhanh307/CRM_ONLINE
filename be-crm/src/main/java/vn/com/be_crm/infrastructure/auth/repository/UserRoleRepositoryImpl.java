package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.auth.entity.UserRole;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.infrastructure.auth.entity.UserRoleHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.UserRoleHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;

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
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            UserRoleHibernate merged = s.merge(mapper.toHibernate(userRole));
            tx.commit();
            return mapper.toDomain(merged);
        }
    }

    /**
     * Thu hồi vai trò khỏi người dùng bằng HQL DELETE.
     *
     * @param userId ID người dùng
     * @param roleId ID vai trò cần thu hồi
     */
    @Override
    public void deleteByUserIdAndRoleId(Long userId, Long roleId) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.createMutationQuery(
                    "DELETE FROM UserRoleHibernate WHERE userId = :userId AND roleId = :roleId")
                    .setParameter("userId", userId)
                    .setParameter("roleId", roleId)
                    .executeUpdate();
            tx.commit();
        }
    }

    /**
     * Lấy tất cả vai trò của một người dùng.
     *
     * @param userId ID người dùng
     * @return danh sách UserRole
     */
    @Override
    public List<UserRole> findByUserId(Long userId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                    "FROM UserRoleHibernate WHERE userId = :userId", UserRoleHibernate.class)
                    .setParameter("userId", userId)
                    .list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }

    /**
     * Lấy danh sách code vai trò của một người dùng bằng HQL subquery.
     *
     * @param userId ID người dùng
     * @return danh sách code vai trò
     */
    @Override
    public List<String> findRoleCodesByUserId(Long userId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                    "SELECT r.code FROM RoleHibernate r WHERE r.id IN " +
                    "(SELECT ur.roleId FROM UserRoleHibernate ur WHERE ur.userId = :userId)",
                    String.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    /**
     * Lấy tất cả UserRole theo roleId.
     *
     * @param roleId ID vai trò
     * @return danh sách UserRole
     */
    @Override
    public List<UserRole> findByRoleId(Long roleId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                    "FROM UserRoleHibernate WHERE roleId = :roleId", UserRoleHibernate.class)
                    .setParameter("roleId", roleId)
                    .list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
