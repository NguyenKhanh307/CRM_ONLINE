package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.auth.entity.RolePermission;
import vn.com.be_crm.domain.auth.repository.IRolePermissionRepository;
import vn.com.be_crm.infrastructure.auth.entity.RolePermissionHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.RolePermissionHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IRolePermissionRepository.
 */
@Repository
public class RolePermissionRepositoryImpl implements IRolePermissionRepository {

    private final SessionFactory sessionFactory;
    private final RolePermissionHibernateMapper mapper;

    /**
     * @param sessionFactory Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public RolePermissionRepositoryImpl(SessionFactory sessionFactory, RolePermissionHibernateMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }

    /**
     * Gán quyền cho vai trò.
     *
     * @param rolePermission entity gán quyền
     * @return entity sau khi lưu
     */
    @Override
    public RolePermission save(RolePermission rolePermission) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            RolePermissionHibernate merged = session.merge(mapper.toHibernate(rolePermission));
            tx.commit();
            return mapper.toDomain(merged);
        }
    }

    /**
     * Thu hồi quyền khỏi vai trò bằng HQL DELETE.
     *
     * @param roleId       ID vai trò
     * @param permissionId ID quyền cần thu hồi
     */
    @Override
    public void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery(
                    "DELETE FROM RolePermissionHibernate WHERE roleId = :roleId AND permissionId = :permId")
                    .setParameter("roleId", roleId)
                    .setParameter("permId", permissionId)
                    .executeUpdate();
            tx.commit();
        }
    }

    /**
     * Lấy tất cả quyền của một vai trò.
     *
     * @param roleId ID vai trò
     * @return danh sách RolePermission
     */
    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM RolePermissionHibernate WHERE roleId = :roleId", RolePermissionHibernate.class)
                    .setParameter("roleId", roleId)
                    .list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
