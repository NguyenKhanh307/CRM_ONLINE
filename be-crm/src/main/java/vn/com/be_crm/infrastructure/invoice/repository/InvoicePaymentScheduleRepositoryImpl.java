package vn.com.be_crm.infrastructure.invoice.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.infrastructure.invoice.entity.InvoicePaymentScheduleHibernate;
import vn.com.be_crm.infrastructure.invoice.mapper.InvoicePaymentScheduleHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IInvoicePaymentScheduleRepository.
 */
@Repository
public class InvoicePaymentScheduleRepositoryImpl implements IInvoicePaymentScheduleRepository {
    private final SessionFactory sf;
    private final InvoicePaymentScheduleHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public InvoicePaymentScheduleRepositoryImpl(SessionFactory sf, InvoicePaymentScheduleHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật InvoicePaymentSchedule. @param ps @return entity sau khi lưu */
    @Override public InvoicePaymentSchedule save(InvoicePaymentSchedule ps) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InvoicePaymentScheduleHibernate m = s.merge(mapper.toHibernate(ps));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm InvoicePaymentSchedule theo ID. @param id @return Optional */
    @Override public Optional<InvoicePaymentSchedule> findById(Long id) {
        try (Session s = sf.openSession()) {
            InvoicePaymentScheduleHibernate h = s.find(InvoicePaymentScheduleHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa InvoicePaymentSchedule. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InvoicePaymentScheduleHibernate h = s.find(InvoicePaymentScheduleHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách InvoicePaymentSchedule theo invoiceId. @param invoiceId @return danh sách */
    @Override public List<InvoicePaymentSchedule> findAllByInvoiceId(Long invoiceId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM InvoicePaymentScheduleHibernate WHERE invoiceId = :oid", InvoicePaymentScheduleHibernate.class)
                    .setParameter("oid", invoiceId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
