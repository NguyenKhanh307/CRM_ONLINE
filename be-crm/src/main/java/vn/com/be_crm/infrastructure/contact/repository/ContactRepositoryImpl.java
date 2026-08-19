package vn.com.be_crm.infrastructure.contact.repository;

import vn.com.be_crm.core.util.ListQueryUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.infrastructure.contact.entity.ContactHibernate;
import vn.com.be_crm.infrastructure.contact.mapper.ContactHibernateMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IContactRepository.
 * Soft delete: deleteById set deleted_at = now().
 */
@Repository
public class ContactRepositoryImpl implements IContactRepository {
    private final SessionFactory sf;
    private final ContactHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public ContactRepositoryImpl(SessionFactory sf, ContactHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật Contact. @param c domain entity @return entity sau khi lưu */
    @Override public Contact save(Contact c) {
        return TxSupport.write(sf, s -> {
            ContactHibernate m = s.merge(mapper.toHibernate(c));
            return mapper.toDomain(m);
        });
    }

    /** Tìm Contact theo email (chưa xóa mềm). @param email email @return Optional */
    @Override public Optional<Contact> findByEmail(String email) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM ContactHibernate WHERE email = :email AND deletedAt IS NULL", ContactHibernate.class)
                    .setParameter("email", email).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /** Tìm Contact theo ID — chỉ trả về nếu chưa xóa mềm. @param id ID @return Optional */
    @Override public Optional<Contact> findById(Long id) {
        return TxSupport.read(sf, s -> {
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /** Xóa mềm Contact, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        TxSupport.writeVoid(sf, s -> {
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            });
    }

    /** Lấy danh sách Contact trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        return TxSupport.read(sf, s -> {
            // Mốc 30 ngày: chỉ hiện bản ghi đã xóa trong 30 ngày gần đây
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            // Không phải admin → chỉ xem bản ghi do chính mình xóa
            String userFilter = isAdmin ? "" : " AND c.deleted_by = :userId";
            // Native query LEFT JOIN users để lấy tên người xóa
            String sql = "SELECT c.id, c.full_name, c.deleted_at, u.full_name FROM contacts c" +
                    " LEFT JOIN users u ON u.id = c.deleted_by" +
                    " WHERE c.deleted_at IS NOT NULL AND c.deleted_at >= :cutoff AND c.is_purged = 0" +
                    userFilter + " ORDER BY c.deleted_at DESC";
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
            String countSql = "SELECT COUNT(*) FROM contacts c WHERE c.deleted_at IS NOT NULL AND c.deleted_at >= :cutoff AND c.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        });
    }

    /** Khôi phục Contact từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            });
    }

    /** Ẩn Contact khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            });
    }

    /** Lấy danh sách Contact chưa xóa có phân trang. @param r tham số phân trang @return PageResult */
    @Override public PageResult<Contact> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String ownerFilter = r.getOwnerId() != null ? " AND assignedUserId = :ownerId" : "";
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "fullName", "email");
            Boolean statusVal = r.getStatus() == null || r.getStatus().isBlank() ? null : Boolean.valueOf(r.getStatus());
            String statusFilter = statusVal != null ? " AND isPrimary = :status" : "";
            // Thu hẹp theo khách hàng — ô chọn Liên hệ trong form dùng để chỉ hiện liên hệ của khách đang chọn
            String customerFilter = r.getCustomerId() != null ? " AND customerId = :customerId" : "";
            String where = " WHERE deletedAt IS NULL" + yearFilter + ownerFilter + searchFilter + statusFilter + customerFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(r.getSortBy(), "createdAt") + " " + ListQueryUtils.safeSortDir(r.getSortDir());
            var q = s.createQuery("FROM ContactHibernate" + where + orderBy, ContactHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM ContactHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (r.getDataAccessFromYear() != null) query.setParameter("fromYear", r.getDataAccessFromYear());
                if (r.getOwnerId() != null) query.setParameter("ownerId", r.getOwnerId());
                if (!searchFilter.isEmpty()) query.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                if (statusVal != null) query.setParameter("status", statusVal);
                if (r.getCustomerId() != null) query.setParameter("customerId", r.getCustomerId());
            }
            List<Contact> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Contact>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }
}
