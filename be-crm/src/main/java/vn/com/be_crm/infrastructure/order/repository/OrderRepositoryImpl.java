package vn.com.be_crm.infrastructure.order.repository;

import vn.com.be_crm.core.audit.CurrentUserHolder;
import vn.com.be_crm.core.util.ListQueryUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.infrastructure.order.entity.OrderHibernate;
import vn.com.be_crm.infrastructure.order.entity.OrderItemHibernate;
import vn.com.be_crm.infrastructure.order.mapper.OrderHibernateMapper;
import vn.com.be_crm.infrastructure.order.mapper.OrderItemHibernateMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IOrderRepository.
 */
@Repository
public class OrderRepositoryImpl implements IOrderRepository {
    private final SessionFactory sf;
    private final OrderHibernateMapper mapper;
    private final OrderItemHibernateMapper itemMapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper @param itemMapper mapper dòng hàng */
    public OrderRepositoryImpl(SessionFactory sf, OrderHibernateMapper mapper,
                               OrderItemHibernateMapper itemMapper) {
        this.sf = sf; this.mapper = mapper; this.itemMapper = itemMapper;
    }

    /** Lưu mới hoặc cập nhật Order. @param o @return entity sau khi lưu */
    @Override public Order save(Order o) {
        return TxSupport.write(sf, s -> {
            OrderHibernate m = s.merge(mapper.toHibernate(o));
            return mapper.toDomain(m);
        });
    }

    /** Tìm Order theo mã (chưa xóa mềm). @param code mã @return Optional */
    @Override public Optional<Order> findByCode(String code) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM OrderHibernate WHERE code = :code AND deletedAt IS NULL", OrderHibernate.class)
                    .setParameter("code", code).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /**
     * Lưu Order kèm danh sách dòng hàng trong MỘT transaction.
     * @param o     domain entity đơn hàng
     * @param items danh sách dòng hàng
     * @return đơn hàng sau khi lưu
     */
    @Override public Order saveWithItems(Order o, List<OrderItem> items) {
        return TxSupport.write(sf, s -> {
            // Lưu header đơn hàng trước để lấy ID
            OrderHibernate m = s.merge(mapper.toHibernate(o));
            // Gán orderId vừa có cho từng dòng hàng rồi lưu trong cùng transaction
            for (OrderItem item : items) {
                OrderItemHibernate ih = itemMapper.toHibernate(item);
                ih.setOrderId(m.getId());
                s.merge(ih);
            }
            // Commit và trả về domain entity đã lưu
            return mapper.toDomain(m);
        });
    }

    /** Tìm Order theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Order> findById(Long id) {
        return TxSupport.read(sf, s -> {
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /** Xóa mềm Order, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        TxSupport.writeVoid(sf, s -> {
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            });
    }

    /** Lấy danh sách Order trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        return TxSupport.read(sf, s -> {
            // Mốc 30 ngày: chỉ hiện bản ghi đã xóa trong 30 ngày gần đây
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            // Không phải admin → chỉ xem bản ghi do chính mình xóa
            String userFilter = isAdmin ? "" : " AND o.deleted_by = :userId";
            // Native query LEFT JOIN users để lấy tên người xóa
            String sql = "SELECT o.id, o.code, o.deleted_at, u.full_name FROM orders o" +
                    " LEFT JOIN users u ON u.id = o.deleted_by" +
                    " WHERE o.deleted_at IS NOT NULL AND o.deleted_at >= :cutoff AND o.is_purged = 0" +
                    userFilter + " ORDER BY o.deleted_at DESC";
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
            String countSql = "SELECT COUNT(*) FROM orders o WHERE o.deleted_at IS NOT NULL AND o.deleted_at >= :cutoff AND o.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        });
    }

    /** Khôi phục Order từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            });
    }

    /** Ẩn Order khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            });
    }

    /** Bàn giao toàn bộ Order của fromUserId sang toUserId. @param fromUserId @param toUserId */
    @Override public void handoverAll(Long fromUserId, Long toUserId) {
        TxSupport.writeVoid(sf, s -> {
            // Bàn giao đi bằng native SQL (bypass Hibernate) → phải tự ghi updated_by/updated_at
            s.createNativeQuery("UPDATE orders SET owner_id = :toUserId, updated_by = :actor, updated_at = NOW() WHERE owner_id = :fromUserId AND deleted_at IS NULL")
                    .setParameter("toUserId", toUserId).setParameter("fromUserId", fromUserId)
                    .setParameter("actor", CurrentUserHolder.get()).executeUpdate();
            });
    }

    /** Bàn giao hàng loạt Order sang owner mới. @param ids IDs @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền admin/manager */
    @Override public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty()) return;
        TxSupport.writeVoid(sf, s -> {
            String ownerFilter = isAdminOrManager ? "" : " AND owner_id = :currentUserId";
            String sql = "UPDATE orders SET owner_id = :toUserId, updated_by = :actor, updated_at = NOW() WHERE id IN (:ids) AND deleted_at IS NULL" + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids)
                    .setParameter("actor", currentUserId);
            if (!isAdminOrManager) q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
            });
    }

    /** Lấy danh sách Order chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Order> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String ownerFilter = r.getOwnerId() != null ? " AND ownerId = :ownerId" : "";
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "code");
            var statusVal = ListQueryUtils.parseEnum(vn.com.be_crm.domain.order.enums.OrderStatus.class, r.getStatus());
            String statusFilter = statusVal != null ? " AND status = :status" : "";
            String where = " WHERE deletedAt IS NULL" + yearFilter + ownerFilter + searchFilter + statusFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(r.getSortBy(), "createdAt") + " " + ListQueryUtils.safeSortDir(r.getSortDir());
            var q = s.createQuery("FROM OrderHibernate" + where + orderBy, OrderHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM OrderHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (r.getDataAccessFromYear() != null) query.setParameter("fromYear", r.getDataAccessFromYear());
                if (r.getOwnerId() != null) query.setParameter("ownerId", r.getOwnerId());
                if (!searchFilter.isEmpty()) query.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                if (statusVal != null) query.setParameter("status", statusVal);
            }
            List<Order> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Order>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }
}
