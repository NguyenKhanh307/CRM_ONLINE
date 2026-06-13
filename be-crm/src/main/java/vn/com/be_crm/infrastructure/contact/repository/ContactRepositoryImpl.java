package vn.com.be_crm.infrastructure.contact.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.entity.ContactPhone;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.infrastructure.contact.entity.ContactHibernate;
import vn.com.be_crm.infrastructure.contact.entity.ContactPhoneHibernate;
import vn.com.be_crm.infrastructure.contact.mapper.ContactHibernateMapper;
import vn.com.be_crm.infrastructure.contact.mapper.ContactPhoneHibernateMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IContactRepository.
 * Soft delete: deleteById set deleted_at = now().
 */
@Repository
public class ContactRepositoryImpl implements IContactRepository {
    private final SessionFactory sf;
    private final ContactHibernateMapper mapper;
    private final ContactPhoneHibernateMapper phoneMapper;

    /**
     * @param sf          Hibernate SessionFactory
     * @param mapper      mapper domain ↔ hibernate
     * @param phoneMapper mapper số điện thoại domain ↔ hibernate
     */
    public ContactRepositoryImpl(SessionFactory sf, ContactHibernateMapper mapper,
                                 ContactPhoneHibernateMapper phoneMapper) {
        this.sf = sf; this.mapper = mapper; this.phoneMapper = phoneMapper;
    }

    /** Lưu mới hoặc cập nhật Contact. @param c domain entity @return entity sau khi lưu */
    @Override public Contact save(Contact c) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactHibernate m = s.merge(mapper.toHibernate(c));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /**
     * Lưu Contact kèm danh sách số điện thoại trong MỘT transaction.
     * @param c      domain entity liên hệ
     * @param phones danh sách số điện thoại
     * @return liên hệ sau khi lưu
     */
    @Override public Contact saveWithPhones(Contact c, List<ContactPhone> phones) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactHibernate m = s.merge(mapper.toHibernate(c));
            for (ContactPhone p : phones) {
                ContactPhoneHibernate ph = phoneMapper.toHibernate(p);
                ph.setContactId(m.getId());
                s.merge(ph);
            }
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Contact theo ID — chỉ trả về nếu chưa xóa mềm. @param id ID @return Optional */
    @Override public Optional<Contact> findById(Long id) {
        try (Session s = sf.openSession()) {
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Contact, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Contact trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        try (Session s = sf.openSession()) {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            String userFilter = isAdmin ? "" : " AND c.deleted_by = :userId";
            String sql = "SELECT c.id, c.full_name, c.deleted_at, u.full_name FROM contacts c" +
                    " LEFT JOIN users u ON u.id = c.deleted_by" +
                    " WHERE c.deleted_at IS NOT NULL AND c.deleted_at >= :cutoff AND c.is_purged = 0" +
                    userFilter + " ORDER BY c.deleted_at DESC";
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
            String countSql = "SELECT COUNT(*) FROM contacts c WHERE c.deleted_at IS NOT NULL AND c.deleted_at >= :cutoff AND c.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        }
    }

    /** Khôi phục Contact từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            tx.commit();
        }
    }

    /** Ẩn Contact khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Contact chưa xóa có phân trang. @param r tham số phân trang @return PageResult */
    @Override public PageResult<Contact> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            var q = s.createQuery("FROM ContactHibernate WHERE deletedAt IS NULL" + yearFilter + " ORDER BY " + r.getSortBy() + " " + r.getSortDir(), ContactHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            if (r.getDataAccessFromYear() != null) q.setParameter("fromYear", r.getDataAccessFromYear());
            List<Contact> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            var cq = s.createQuery("SELECT COUNT(c) FROM ContactHibernate c WHERE c.deletedAt IS NULL" + (r.getDataAccessFromYear() != null ? " AND YEAR(c.createdAt) >= :fromYear" : ""), Long.class);
            if (r.getDataAccessFromYear() != null) cq.setParameter("fromYear", r.getDataAccessFromYear());
            long total = cq.uniqueResult();
            return PageResult.<Contact>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
