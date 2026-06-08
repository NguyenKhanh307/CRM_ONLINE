package vn.com.be_crm.infrastructure.quotation.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationHibernate;
import vn.com.be_crm.infrastructure.quotation.mapper.QuotationHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IQuotationRepository.
 */
@Repository
public class QuotationRepositoryImpl implements IQuotationRepository {
    private final SessionFactory sf;
    private final QuotationHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public QuotationRepositoryImpl(SessionFactory sf, QuotationHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật Quotation. @param q @return entity sau khi lưu */
    @Override public Quotation save(Quotation q) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            QuotationHibernate m = s.merge(mapper.toHibernate(q));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Quotation theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Quotation> findById(Long id) {
        try (Session s = sf.openSession()) {
            QuotationHibernate h = s.find(QuotationHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Quotation. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            QuotationHibernate h = s.find(QuotationHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Quotation chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Quotation> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<Quotation> items = s.createQuery(
                    "FROM QuotationHibernate WHERE deletedAt IS NULL ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    QuotationHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(q) FROM QuotationHibernate q WHERE q.deletedAt IS NULL", Long.class).uniqueResult();
            return PageResult.<Quotation>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
