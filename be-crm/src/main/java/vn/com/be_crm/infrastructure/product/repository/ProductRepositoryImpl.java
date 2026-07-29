package vn.com.be_crm.infrastructure.product.repository;

import vn.com.be_crm.infrastructure.shared.util.ListQueryUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

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
        return TxSupport.write(sf, s -> {
            ProductHibernate m = s.merge(mapper.toHibernate(p));
            return mapper.toDomain(m);
        });
    }

    /** Tìm Product theo ID — chỉ trả về nếu chưa xóa mềm. @param id ID @return Optional */
    @Override public Optional<Product> findById(Long id) {
        return TxSupport.read(sf, s -> {
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /** Tìm Product theo SKU (chưa xóa mềm). @param sku SKU @return Optional */
    @Override public Optional<Product> findBySku(String sku) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM ProductHibernate WHERE sku = :sku AND deletedAt IS NULL", ProductHibernate.class)
                    .setParameter("sku", sku).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /** Lấy toàn bộ Product thuộc 1 danh mục (chưa xóa mềm). @param categoryId ID danh mục @return danh sách */
    @Override public List<Product> findAllByCategoryId(Long categoryId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM ProductHibernate WHERE categoryId = :categoryId AND deletedAt IS NULL", ProductHibernate.class)
                    .setParameter("categoryId", categoryId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }

    /** Xóa mềm Product, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        TxSupport.writeVoid(sf, s -> {
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            });
    }

    /** Lấy danh sách Product trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        return TxSupport.read(sf, s -> {
            // Mốc 30 ngày: chỉ hiện bản ghi đã xóa trong 30 ngày gần đây
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            // Không phải admin → chỉ xem bản ghi do chính mình xóa
            String userFilter = isAdmin ? "" : " AND p.deleted_by = :userId";
            // Native query LEFT JOIN users để lấy tên người xóa
            String sql = "SELECT p.id, p.name, p.deleted_at, u.full_name FROM products p" +
                    " LEFT JOIN users u ON u.id = p.deleted_by" +
                    " WHERE p.deleted_at IS NOT NULL AND p.deleted_at >= :cutoff AND p.is_purged = 0" +
                    userFilter + " ORDER BY p.deleted_at DESC";
            // Chạy query phân trang rồi map Object[] → DeletedItemResult (xử lý Timestamp của TiDB)
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
            // Query đếm tổng số bản ghi đã xóa để phân trang
            String countSql = "SELECT COUNT(*) FROM products p WHERE p.deleted_at IS NOT NULL AND p.deleted_at >= :cutoff AND p.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        });
    }

    /** Khôi phục Product từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            });
    }

    /** Ẩn Product khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            });
    }

    /** Lấy danh sách Product chưa xóa có phân trang. @param r tham số phân trang @return PageResult */
    @Override public PageResult<Product> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "sku", "name");
            Boolean statusVal = r.getStatus() == null || r.getStatus().isBlank() ? null : Boolean.valueOf(r.getStatus());
            String statusFilter = statusVal != null ? " AND isActive = :status" : "";
            String where = " WHERE deletedAt IS NULL" + yearFilter + searchFilter + statusFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(r.getSortBy(), "createdAt") + " " + ListQueryUtils.safeSortDir(r.getSortDir());
            var q = s.createQuery("FROM ProductHibernate" + where + orderBy, ProductHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM ProductHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (r.getDataAccessFromYear() != null) query.setParameter("fromYear", r.getDataAccessFromYear());
                if (!searchFilter.isEmpty()) query.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                if (statusVal != null) query.setParameter("status", statusVal);
            }
            List<Product> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Product>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }
}
