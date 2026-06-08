package vn.com.be_crm.infrastructure.quotation.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationItemHibernate;
import vn.com.be_crm.infrastructure.quotation.mapper.QuotationItemHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IQuotationItemRepository.
 */
@Repository
public class QuotationItemRepositoryImpl implements IQuotationItemRepository {
    private final SessionFactory sf;
    private final QuotationItemHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public QuotationItemRepositoryImpl(SessionFactory sf, QuotationItemHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật QuotationItem. @param i @return entity sau khi lưu */
    @Override public QuotationItem save(QuotationItem i) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            QuotationItemHibernate m = s.merge(mapper.toHibernate(i));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm QuotationItem theo ID. @param id @return Optional */
    @Override public Optional<QuotationItem> findById(Long id) {
        try (Session s = sf.openSession()) {
            QuotationItemHibernate h = s.find(QuotationItemHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa QuotationItem theo ID. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            QuotationItemHibernate h = s.find(QuotationItemHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách QuotationItem theo quotationId. @param quotationId @return danh sách */
    @Override public List<QuotationItem> findAllByQuotationId(Long quotationId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM QuotationItemHibernate WHERE quotationId = :qid", QuotationItemHibernate.class)
                    .setParameter("qid", quotationId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
