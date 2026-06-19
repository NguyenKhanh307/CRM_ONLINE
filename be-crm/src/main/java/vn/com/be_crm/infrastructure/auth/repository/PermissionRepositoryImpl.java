package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.auth.entity.Permission;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;
import vn.com.be_crm.infrastructure.auth.entity.PermissionHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.PermissionHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IPermissionRepository.
 */
@Repository
public class PermissionRepositoryImpl implements IPermissionRepository {

    private final SessionFactory sf;
    private final PermissionHibernateMapper mapper;

    /**
     * @param sf Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public PermissionRepositoryImpl(SessionFactory sf, PermissionHibernateMapper mapper) {
        this.sf = sf;
        this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật Permission.
     *
     * @param permission domain entity cần lưu
     * @return domain entity sau khi lưu
     */
    @Override
    public Permission save(Permission permission) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PermissionHibernate merged = s.merge(mapper.toHibernate(permission));
            tx.commit();
            return mapper.toDomain(merged);
        }
    }

    /**
     * Tìm Permission theo ID.
     *
     * @param id ID quyền
     * @return Optional chứa Permission nếu tìm thấy
     */
    @Override
    public Optional<Permission> findById(Long id) {
        try (Session s = sf.openSession()) {
            PermissionHibernate h = s.find(PermissionHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /**
     * Xóa Permission theo ID. Không làm gì nếu không tìm thấy.
     *
     * @param id ID quyền cần xóa
     */
    @Override
    public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PermissionHibernate h = s.find(PermissionHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /**
     * Lấy danh sách Permission có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách Permission
     */
    @Override
    public PageResult<Permission> findAll(PageRequest request) {
        try (Session s = sf.openSession()) {
            String hql = "FROM PermissionHibernate ORDER BY " + request.getSortBy() + " " + request.getSortDir();
            List<Permission> items = s.createQuery(hql, PermissionHibernate.class)
                    .setFirstResult(request.getOffset())
                    .setMaxResults(request.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(p) FROM PermissionHibernate p", Long.class).uniqueResult();
            return PageResult.<Permission>builder()
                    .items(items).total(total).page(request.getPage()).size(request.getSize()).build();
        }
    }

    /**
     * Lấy tất cả permission code của user thông qua user_roles → role_permissions.
     *
     * @param userId ID người dùng
     * @return danh sách code (vd: "lead.view", "order.create")
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> findCodesByUserId(Long userId) {
        try (Session s = sf.openSession()) {
            String sql = """
                    SELECT DISTINCT p.code
                    FROM permissions p
                    JOIN role_permissions rp ON p.id = rp.permission_id
                    JOIN user_roles ur ON rp.role_id = ur.role_id
                    WHERE ur.user_id = :userId
                    ORDER BY p.code
                    """;
            return s.createNativeQuery(sql, String.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }
}
