package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.lead.entity.LeadTransfer;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadTransferHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadTransferHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của ILeadTransferRepository.
 */
@Repository
public class LeadTransferRepositoryImpl implements ILeadTransferRepository {
    private final SessionFactory sf;
    private final LeadTransferHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public LeadTransferRepositoryImpl(SessionFactory sf, LeadTransferHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật LeadTransfer. @param t domain entity @return entity sau khi lưu */
    @Override public LeadTransfer save(LeadTransfer t) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadTransferHibernate m = s.merge(mapper.toHibernate(t));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm LeadTransfer theo ID. @param id ID @return Optional */
    @Override public Optional<LeadTransfer> findById(Long id) {
        try (Session s = sf.openSession()) {
            LeadTransferHibernate h = s.find(LeadTransferHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa LeadTransfer theo ID. @param id ID */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadTransferHibernate h = s.find(LeadTransferHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách LeadTransfer theo leadId. @param leadId ID @return danh sách */
    @Override public List<LeadTransfer> findAllByLeadId(Long leadId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM LeadTransferHibernate WHERE leadId = :lid", LeadTransferHibernate.class)
                    .setParameter("lid", leadId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
