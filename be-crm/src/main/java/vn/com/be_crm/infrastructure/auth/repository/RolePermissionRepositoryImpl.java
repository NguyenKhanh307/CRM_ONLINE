package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.auth.entity.RolePermission;
import vn.com.be_crm.domain.auth.repository.IRolePermissionRepository;
import vn.com.be_crm.infrastructure.auth.entity.RolePermissionHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.RolePermissionHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Hibernate implementation của IRolePermissionRepository.
 */
@Repository
public class RolePermissionRepositoryImpl implements IRolePermissionRepository {

    private final SessionFactory sf;
    private final RolePermissionHibernateMapper mapper;

    /**
     * @param sf Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public RolePermissionRepositoryImpl(SessionFactory sf, RolePermissionHibernateMapper mapper) {
        this.sf = sf;
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
        return TxSupport.write(sf, s -> {
            RolePermissionHibernate merged = s.merge(mapper.toHibernate(rolePermission));
            return mapper.toDomain(merged);
        });
    }

    /**
     * Thu hồi quyền khỏi vai trò bằng HQL DELETE.
     *
     * @param roleId       ID vai trò
     * @param permissionId ID quyền cần thu hồi
     */
    @Override
    public void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId) {
        TxSupport.writeVoid(sf, s -> {
            s.createMutationQuery(
                    "DELETE FROM RolePermissionHibernate WHERE roleId = :roleId AND permissionId = :permId")
                    .setParameter("roleId", roleId)
                    .setParameter("permId", permissionId)
                    .executeUpdate();
            });
    }

    /**
     * Lấy tất cả quyền của một vai trò.
     *
     * @param roleId ID vai trò
     * @return danh sách RolePermission
     */
    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery(
                    "FROM RolePermissionHibernate WHERE roleId = :roleId", RolePermissionHibernate.class)
                    .setParameter("roleId", roleId)
                    .list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }
}
