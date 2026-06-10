package vn.com.be_crm.infrastructure.order.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.infrastructure.order.entity.OrderHibernate;
import vn.com.be_crm.infrastructure.order.mapper.OrderHibernateMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IOrderRepository.
 */
@Repository
public class OrderRepositoryImpl implements IOrderRepository {
    private final SessionFactory sf;
    private final OrderHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public OrderRepositoryImpl(SessionFactory sf, OrderHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật Order. @param o @return entity sau khi lưu */
    @Override public Order save(Order o) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderHibernate m = s.merge(mapper.toHibernate(o));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Order theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Order> findById(Long id) {
        try (Session s = sf.openSession()) {
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Order, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Order trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        try (Session s = sf.openSession()) {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            String userFilter = isAdmin ? "" : " AND o.deleted_by = :userId";
            String sql = "SELECT o.id, o.code, o.deleted_at, u.full_name FROM orders o" +
                    " LEFT JOIN users u ON u.id = o.deleted_by" +
                    " WHERE o.deleted_at IS NOT NULL AND o.deleted_at >= :cutoff AND o.is_purged = 0" +
                    userFilter + " ORDER BY o.deleted_at DESC";
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
            String countSql = "SELECT COUNT(*) FROM orders o WHERE o.deleted_at IS NOT NULL AND o.deleted_at >= :cutoff AND o.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        }
    }

    /** Khôi phục Order từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            tx.commit();
        }
    }

    /** Ẩn Order khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            tx.commit();
        }
    }

    /** Bàn giao toàn bộ Order của fromUserId sang toUserId. @param fromUserId @param toUserId */
    @Override public void handoverAll(Long fromUserId, Long toUserId) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.createNativeQuery("UPDATE orders SET owner_id = :toUserId WHERE owner_id = :fromUserId AND deleted_at IS NULL")
                    .setParameter("toUserId", toUserId).setParameter("fromUserId", fromUserId).executeUpdate();
            tx.commit();
        }
    }

    /** Bàn giao hàng loạt Order sang owner mới. @param ids IDs @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền admin/manager */
    @Override public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty()) return;
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            String ownerFilter = isAdminOrManager ? "" : " AND owner_id = :currentUserId";
            String sql = "UPDATE orders SET owner_id = :toUserId WHERE id IN (:ids) AND deleted_at IS NULL" + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids);
            if (!isAdminOrManager) q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
            tx.commit();
        }
    }

    /** Lấy danh sách Order chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Order> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            var q = s.createQuery("FROM OrderHibernate WHERE deletedAt IS NULL" + yearFilter + " ORDER BY " + r.getSortBy() + " " + r.getSortDir(), OrderHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            if (r.getDataAccessFromYear() != null) q.setParameter("fromYear", r.getDataAccessFromYear());
            List<Order> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            var cq = s.createQuery("SELECT COUNT(o) FROM OrderHibernate o WHERE o.deletedAt IS NULL" + (r.getDataAccessFromYear() != null ? " AND YEAR(o.createdAt) >= :fromYear" : ""), Long.class);
            if (r.getDataAccessFromYear() != null) cq.setParameter("fromYear", r.getDataAccessFromYear());
            long total = cq.uniqueResult();
            return PageResult.<Order>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
