package vn.com.be_crm.infrastructure.invoice.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.invoice.entity.InvoiceRevenueRecord;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRevenueRecordRepository;
import vn.com.be_crm.infrastructure.invoice.entity.InvoiceRevenueRecordHibernate;
import vn.com.be_crm.infrastructure.invoice.mapper.InvoiceRevenueRecordHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IInvoiceRevenueRecordRepository.
 */
@Repository
public class InvoiceRevenueRecordRepositoryImpl implements IInvoiceRevenueRecordRepository {
    private final SessionFactory sf;
    private final InvoiceRevenueRecordHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public InvoiceRevenueRecordRepositoryImpl(SessionFactory sf, InvoiceRevenueRecordHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu bản ghi doanh thu. @param r @return entity sau khi lưu */
    @Override public InvoiceRevenueRecord save(InvoiceRevenueRecord r) {
        return TxSupport.write(sf, s -> {
            InvoiceRevenueRecordHibernate m = s.merge(mapper.toHibernate(r));
            return mapper.toDomain(m);
        });
    }

    /** Tìm InvoiceRevenueRecord theo ID. @param id @return Optional */
    @Override public Optional<InvoiceRevenueRecord> findById(Long id) {
        return TxSupport.read(sf, s -> {
            InvoiceRevenueRecordHibernate h = s.find(InvoiceRevenueRecordHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        });
    }

    /** Xóa InvoiceRevenueRecord. @param id */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            InvoiceRevenueRecordHibernate h = s.find(InvoiceRevenueRecordHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /** Lấy danh sách bản ghi doanh thu theo invoiceId. @param invoiceId @return danh sách */
    @Override public List<InvoiceRevenueRecord> findAllByInvoiceId(Long invoiceId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM InvoiceRevenueRecordHibernate WHERE invoiceId = :oid", InvoiceRevenueRecordHibernate.class)
                    .setParameter("oid", invoiceId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }
}
