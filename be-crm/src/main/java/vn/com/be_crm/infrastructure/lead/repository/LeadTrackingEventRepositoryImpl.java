package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadTrackingEventHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadTrackingEventHibernateMapper;
import vn.com.be_crm.core.tx.impl.TxSupport;

// impl Hibernate của ILeadTrackingEventRepository
@Repository
public class LeadTrackingEventRepositoryImpl implements ILeadTrackingEventRepository {
    private final SessionFactory sf;
    private final LeadTrackingEventHibernateMapper mapper;

    public LeadTrackingEventRepositoryImpl(SessionFactory sf, LeadTrackingEventHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    @Override public LeadTrackingEvent save(LeadTrackingEvent e) {
        return TxSupport.write(sf, s -> {
            LeadTrackingEventHibernate m = s.merge(mapper.toHibernate(e));
            return mapper.toDomain(m);
        });
    }
}
