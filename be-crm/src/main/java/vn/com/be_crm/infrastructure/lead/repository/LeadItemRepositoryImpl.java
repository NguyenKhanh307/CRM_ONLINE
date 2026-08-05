package vn.com.be_crm.infrastructure.lead.repository;

import org.springframework.stereotype.Repository;
import org.hibernate.SessionFactory;
import vn.com.be_crm.core.tx.impl.TxSupport;
import vn.com.be_crm.domain.lead.entity.LeadItem;
import vn.com.be_crm.domain.lead.repository.ILeadItemRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadItemHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadItemHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;

// impl Hibernate của ILeadItemRepository
@Repository
public class LeadItemRepositoryImpl implements ILeadItemRepository {
    private final SessionFactory sf;
    private final LeadItemHibernateMapper mapper;

    public LeadItemRepositoryImpl(SessionFactory sf, LeadItemHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    @Override public LeadItem save(LeadItem e) {
        return TxSupport.write(sf, s -> {
            LeadItemHibernate m = s.merge(mapper.toHibernate(e));
            return mapper.toDomain(m);
        });
    }

    @Override public List<LeadItem> findAllByLeadId(Long leadId) {
        return TxSupport.read(sf, s ->
                s.createQuery("FROM LeadItemHibernate WHERE leadId = :lid ORDER BY id DESC", LeadItemHibernate.class)
                        .setParameter("lid", leadId).list().stream().map(mapper::toDomain).collect(Collectors.toList()));
    }
}
