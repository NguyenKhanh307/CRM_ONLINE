package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadTrackingEventHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadTrackingEventHibernateMapper;

/**
 * Hibernate implementation của ILeadTrackingEventRepository.
 */
@Repository
public class LeadTrackingEventRepositoryImpl implements ILeadTrackingEventRepository {
    private final SessionFactory sf;
    private final LeadTrackingEventHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public LeadTrackingEventRepositoryImpl(SessionFactory sf, LeadTrackingEventHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu một sự kiện tracking. @param e domain entity @return entity sau khi lưu */
    @Override public LeadTrackingEvent save(LeadTrackingEvent e) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadTrackingEventHibernate m = s.merge(mapper.toHibernate(e));
            tx.commit(); return mapper.toDomain(m);
        }
    }
}
