package vn.com.be_crm.infrastructure.invoice.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.infrastructure.invoice.entity.InvoiceHibernate;
import vn.com.be_crm.infrastructure.invoice.entity.InvoiceItemHibernate;
import vn.com.be_crm.infrastructure.invoice.mapper.InvoiceHibernateMapper;
import vn.com.be_crm.infrastructure.invoice.mapper.InvoiceItemHibernateMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IInvoiceRepository.
 */
@Repository
public class InvoiceRepositoryImpl implements IInvoiceRepository {
    private final SessionFactory sf;
    private final InvoiceHibernateMapper mapper;
    private final InvoiceItemHibernateMapper itemMapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper @param itemMapper mapper dòng hàng */
    public InvoiceRepositoryImpl(SessionFactory sf, InvoiceHibernateMapper mapper,
                               InvoiceItemHibernateMapper itemMapper) {
        this.sf = sf; this.mapper = mapper; this.itemMapper = itemMapper;
    }

    /** Lưu mới hoặc cập nhật Invoice. @param o @return entity sau khi lưu */
    @Override public Invoice save(Invoice o) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InvoiceHibernate m = s.merge(mapper.toHibernate(o));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Invoice theo mã (chưa xóa mềm). @param code mã @return Optional */
    @Override public Optional<Invoice> findByCode(String code) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM InvoiceHibernate WHERE code = :code AND deletedAt IS NULL", InvoiceHibernate.class)
                    .setParameter("code", code).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        }
    }

    /**
     * Lưu Invoice kèm danh sách dòng hàng trong MỘT transaction.
     * @param o     domain entity đơn hàng
     * @param items danh sách dòng hàng
     * @return đơn hàng sau khi lưu
     */
    @Override public Invoice saveWithItems(Invoice o, List<InvoiceItem> items) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            // Lưu header đơn hàng trước để lấy ID
            InvoiceHibernate m = s.merge(mapper.toHibernate(o));
            // Gán invoiceId vừa có cho từng dòng hàng rồi lưu trong cùng transaction
            for (InvoiceItem item : items) {
                InvoiceItemHibernate ih = itemMapper.toHibernate(item);
                ih.setInvoiceId(m.getId());
                s.merge(ih);
            }
            // Commit và trả về domain entity đã lưu
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Invoice theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Invoice> findById(Long id) {
        try (Session s = sf.openSession()) {
            InvoiceHibernate h = s.find(InvoiceHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Invoice, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InvoiceHibernate h = s.find(InvoiceHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Invoice trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        try (Session s = sf.openSession()) {
            // Mốc 30 ngày: chỉ hiện bản ghi đã xóa trong 30 ngày gần đây
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            // Không phải admin → chỉ xem bản ghi do chính mình xóa
            String userFilter = isAdmin ? "" : " AND o.deleted_by = :userId";
            // Native query LEFT JOIN users để lấy tên người xóa
            String sql = "SELECT o.id, o.code, o.deleted_at, u.full_name FROM invoices o" +
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
            String countSql = "SELECT COUNT(*) FROM invoices o WHERE o.deleted_at IS NOT NULL AND o.deleted_at >= :cutoff AND o.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        }
    }

    /** Khôi phục Invoice từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InvoiceHibernate h = s.find(InvoiceHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            tx.commit();
        }
    }

    /** Ẩn Invoice khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InvoiceHibernate h = s.find(InvoiceHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            tx.commit();
        }
    }

    /** Bàn giao toàn bộ Invoice của fromUserId sang toUserId. @param fromUserId @param toUserId */
    @Override public void handoverAll(Long fromUserId, Long toUserId) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.createNativeQuery("UPDATE invoices SET owner_id = :toUserId WHERE owner_id = :fromUserId AND deleted_at IS NULL")
                    .setParameter("toUserId", toUserId).setParameter("fromUserId", fromUserId).executeUpdate();
            tx.commit();
        }
    }

    /** Bàn giao hàng loạt Invoice sang owner mới. @param ids IDs @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền admin/manager */
    @Override public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty()) return;
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            String ownerFilter = isAdminOrManager ? "" : " AND owner_id = :currentUserId";
            String sql = "UPDATE invoices SET owner_id = :toUserId WHERE id IN (:ids) AND deleted_at IS NULL" + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids);
            if (!isAdminOrManager) q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
            tx.commit();
        }
    }

    /** Lấy danh sách Invoice chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Invoice> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            var q = s.createQuery("FROM InvoiceHibernate WHERE deletedAt IS NULL" + yearFilter + " ORDER BY " + r.getSortBy() + " " + r.getSortDir(), InvoiceHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            if (r.getDataAccessFromYear() != null) q.setParameter("fromYear", r.getDataAccessFromYear());
            List<Invoice> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            var cq = s.createQuery("SELECT COUNT(o) FROM InvoiceHibernate o WHERE o.deletedAt IS NULL" + (r.getDataAccessFromYear() != null ? " AND YEAR(o.createdAt) >= :fromYear" : ""), Long.class);
            if (r.getDataAccessFromYear() != null) cq.setParameter("fromYear", r.getDataAccessFromYear());
            long total = cq.uniqueResult();
            return PageResult.<Invoice>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
