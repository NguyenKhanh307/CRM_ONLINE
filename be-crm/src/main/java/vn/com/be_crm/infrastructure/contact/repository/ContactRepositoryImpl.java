package vn.com.be_crm.infrastructure.contact.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.infrastructure.contact.entity.ContactHibernate;
import vn.com.be_crm.infrastructure.contact.mapper.ContactHibernateMapper;

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

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public ContactRepositoryImpl(SessionFactory sf, ContactHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật Contact.
     * @param c domain entity @return entity sau khi lưu
     */
    @Override
    public Contact save(Contact c) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactHibernate m = s.merge(mapper.toHibernate(c));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /**
     * Tìm Contact theo ID — chỉ trả về nếu chưa xóa mềm.
     * @param id ID @return Optional
     */
    @Override
    public Optional<Contact> findById(Long id) {
        try (Session s = sf.openSession()) {
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /**
     * Xóa mềm Contact bằng cách set deleted_at = now().
     * @param id ID cần xóa mềm
     */
    @Override
    public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactHibernate h = s.find(ContactHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); s.merge(h); }
            tx.commit();
        }
    }

    /**
     * Lấy danh sách Contact chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<Contact> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<Contact> items = s.createQuery(
                    "FROM ContactHibernate WHERE deletedAt IS NULL ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    ContactHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(c) FROM ContactHibernate c WHERE c.deletedAt IS NULL", Long.class).uniqueResult();
            return PageResult.<Contact>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
