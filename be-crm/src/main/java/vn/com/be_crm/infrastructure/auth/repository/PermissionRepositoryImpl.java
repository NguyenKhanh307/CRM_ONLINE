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

    private final SessionFactory sessionFactory;
    private final PermissionHibernateMapper mapper;

    /**
     * @param sessionFactory Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public PermissionRepositoryImpl(SessionFactory sessionFactory, PermissionHibernateMapper mapper) {
        this.sessionFactory = sessionFactory;
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
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            PermissionHibernate merged = session.merge(mapper.toHibernate(permission));
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
        try (Session session = sessionFactory.openSession()) {
            PermissionHibernate h = session.find(PermissionHibernate.class, id);
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
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            PermissionHibernate h = session.find(PermissionHibernate.class, id);
            if (h != null) session.remove(h);
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
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM PermissionHibernate ORDER BY " + request.getSortBy() + " " + request.getSortDir();
            List<Permission> items = session.createQuery(hql, PermissionHibernate.class)
                    .setFirstResult(request.getOffset())
                    .setMaxResults(request.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = session.createQuery("SELECT COUNT(p) FROM PermissionHibernate p", Long.class).uniqueResult();
            return PageResult.<Permission>builder()
                    .items(items).total(total).page(request.getPage()).size(request.getSize()).build();
        }
    }
}
