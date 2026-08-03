package vn.com.be_crm.infrastructure.quotation.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.quotation.entity.QuotationApproval;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationApprovalHibernate;
import vn.com.be_crm.infrastructure.quotation.mapper.QuotationApprovalHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IQuotationApprovalRepository.
 */
@Repository
public class QuotationApprovalRepositoryImpl implements IQuotationApprovalRepository {
    private final SessionFactory sf;
    private final QuotationApprovalHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public QuotationApprovalRepositoryImpl(SessionFactory sf, QuotationApprovalHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật QuotationApproval. @param a @return entity sau khi lưu */
    @Override public QuotationApproval save(QuotationApproval a) {
        return TxSupport.write(sf, s -> {
            QuotationApprovalHibernate m = s.merge(mapper.toHibernate(a));
            return mapper.toDomain(m);
        });
    }

    /** Tìm QuotationApproval theo ID. @param id @return Optional */
    @Override public Optional<QuotationApproval> findById(Long id) {
        return TxSupport.read(sf, s -> {
            QuotationApprovalHibernate h = s.find(QuotationApprovalHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        });
    }

    /** Xóa QuotationApproval theo ID. @param id */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            QuotationApprovalHibernate h = s.find(QuotationApprovalHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /** Lấy danh sách QuotationApproval theo quotationId. @param quotationId @return danh sách */
    @Override public List<QuotationApproval> findAllByQuotationId(Long quotationId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM QuotationApprovalHibernate WHERE quotationId = :qid", QuotationApprovalHibernate.class)
                    .setParameter("qid", quotationId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }
}
