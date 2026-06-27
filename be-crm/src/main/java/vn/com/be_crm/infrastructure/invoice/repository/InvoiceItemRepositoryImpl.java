package vn.com.be_crm.infrastructure.invoice.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.infrastructure.invoice.entity.InvoiceItemHibernate;
import vn.com.be_crm.infrastructure.invoice.mapper.InvoiceItemHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IInvoiceItemRepository.
 */
@Repository
public class InvoiceItemRepositoryImpl implements IInvoiceItemRepository {
    private final SessionFactory sf;
    private final InvoiceItemHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public InvoiceItemRepositoryImpl(SessionFactory sf, InvoiceItemHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật InvoiceItem. @param i @return entity sau khi lưu */
    @Override public InvoiceItem save(InvoiceItem i) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InvoiceItemHibernate m = s.merge(mapper.toHibernate(i));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm InvoiceItem theo ID. @param id @return Optional */
    @Override public Optional<InvoiceItem> findById(Long id) {
        try (Session s = sf.openSession()) {
            InvoiceItemHibernate h = s.find(InvoiceItemHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa InvoiceItem. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InvoiceItemHibernate h = s.find(InvoiceItemHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách InvoiceItem theo invoiceId. @param invoiceId @return danh sách */
    @Override public List<InvoiceItem> findAllByInvoiceId(Long invoiceId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM InvoiceItemHibernate WHERE invoiceId = :oid", InvoiceItemHibernate.class)
                    .setParameter("oid", invoiceId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
