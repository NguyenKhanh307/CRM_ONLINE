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

    private final SessionFactory sessionFactory;
    private final UserRoleHibernateMapper mapper;

    /**
     * @param sessionFactory Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public UserRoleRepositoryImpl(SessionFactory sessionFactory, UserRoleHibernateMapper mapper) {
        this.sessionFactory = sessionFactory;
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
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            UserRoleHibernate merged = session.merge(mapper.toHibernate(userRole));
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
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery(
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
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
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
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "SELECT r.code FROM RoleHibernate r WHERE r.id IN " +
                    "(SELECT ur.roleId FROM UserRoleHibernate ur WHERE ur.userId = :userId)",
                    String.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }
}
