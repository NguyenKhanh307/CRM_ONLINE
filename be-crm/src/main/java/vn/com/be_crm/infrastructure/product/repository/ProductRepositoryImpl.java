package vn.com.be_crm.infrastructure.product.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.infrastructure.product.entity.ProductHibernate;
import vn.com.be_crm.infrastructure.product.mapper.ProductHibernateMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IProductRepository.
 * Soft delete: deleteById set deleted_at = now().
 */
@Repository
public class ProductRepositoryImpl implements IProductRepository {
    private final SessionFactory sf;
    private final ProductHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public ProductRepositoryImpl(SessionFactory sf, ProductHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật Product. @param p domain entity @return entity sau khi lưu */
    @Override public Product save(Product p) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ProductHibernate m = s.merge(mapper.toHibernate(p));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Product theo ID — chỉ trả về nếu chưa xóa mềm. @param id ID @return Optional */
    @Override public Optional<Product> findById(Long id) {
        try (Session s = sf.openSession()) {
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Product, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Product trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        try (Session s = sf.openSession()) {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            String userFilter = isAdmin ? "" : " AND p.deleted_by = :userId";
            String sql = "SELECT p.id, p.name, p.deleted_at, u.full_name FROM products p" +
                    " LEFT JOIN users u ON u.id = p.deleted_by" +
                    " WHERE p.deleted_at IS NOT NULL AND p.deleted_at >= :cutoff AND p.is_purged = 0" +
                    userFilter + " ORDER BY p.deleted_at DESC";
            var q = s.createNativeQuery(sql, Object[].class)
                    .setParameter("cutoff", cutoff)
                    .setFirstResult(req.getOffset()).setMaxResults(req.getSize());
            if (!isAdmin) q.setParameter("userId", userId);
            List<DeletedItemResult> items = q.list().stream()
                    .map(row -> new DeletedItemResult(
                            ((Number) row[0]).longValue(), (String) row[1],
                            row[2] instanceof Timestamp ts ? ts.toLocalDateTime() : (LocalDateTime) row[2],
                            (String) row[3]))
                    .collect(Collectors.toList());
            String countSql = "SELECT COUNT(*) FROM products p WHERE p.deleted_at IS NOT NULL AND p.deleted_at >= :cutoff AND p.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        }
    }

    /** Khôi phục Product từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            tx.commit();
        }
    }

    /** Ẩn Product khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Product chưa xóa có phân trang. @param r tham số phân trang @return PageResult */
    @Override public PageResult<Product> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            var q = s.createQuery("FROM ProductHibernate WHERE deletedAt IS NULL" + yearFilter + " ORDER BY " + r.getSortBy() + " " + r.getSortDir(), ProductHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            if (r.getDataAccessFromYear() != null) q.setParameter("fromYear", r.getDataAccessFromYear());
            List<Product> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            var cq = s.createQuery("SELECT COUNT(p) FROM ProductHibernate p WHERE p.deletedAt IS NULL" + (r.getDataAccessFromYear() != null ? " AND YEAR(p.createdAt) >= :fromYear" : ""), Long.class);
            if (r.getDataAccessFromYear() != null) cq.setParameter("fromYear", r.getDataAccessFromYear());
            long total = cq.uniqueResult();
            return PageResult.<Product>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
