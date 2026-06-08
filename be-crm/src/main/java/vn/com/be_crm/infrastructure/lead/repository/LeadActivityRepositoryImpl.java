package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.lead.entity.LeadActivity;
import vn.com.be_crm.domain.lead.repository.ILeadActivityRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadActivityHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadActivityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của ILeadActivityRepository.
 */
@Repository
public class LeadActivityRepositoryImpl implements ILeadActivityRepository {
    private final SessionFactory sf;
    private final LeadActivityHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public LeadActivityRepositoryImpl(SessionFactory sf, LeadActivityHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật LeadActivity. @param a domain entity @return entity sau khi lưu */
    @Override public LeadActivity save(LeadActivity a) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadActivityHibernate m = s.merge(mapper.toHibernate(a));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm LeadActivity theo ID. @param id ID @return Optional */
    @Override public Optional<LeadActivity> findById(Long id) {
        try (Session s = sf.openSession()) {
            LeadActivityHibernate h = s.find(LeadActivityHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa LeadActivity theo ID. @param id ID */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadActivityHibernate h = s.find(LeadActivityHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách LeadActivity theo leadId. @param leadId ID @return danh sách */
    @Override public List<LeadActivity> findAllByLeadId(Long leadId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM LeadActivityHibernate WHERE leadId = :lid", LeadActivityHibernate.class)
                    .setParameter("lid", leadId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
