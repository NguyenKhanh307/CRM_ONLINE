package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.lead.entity.LeadTransfer;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadTransferHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadTransferHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

// impl Hibernate của ILeadTransferRepository
@Repository
public class LeadTransferRepositoryImpl implements ILeadTransferRepository {
    private final SessionFactory sf;
    private final LeadTransferHibernateMapper mapper;

    public LeadTransferRepositoryImpl(SessionFactory sf, LeadTransferHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    @Override public LeadTransfer save(LeadTransfer t) {
        return TxSupport.write(sf, s -> {
            LeadTransferHibernate m = s.merge(mapper.toHibernate(t));
            return mapper.toDomain(m);
        });
    }

    @Override public Optional<LeadTransfer> findById(Long id) {
        return TxSupport.read(sf, s -> {
            LeadTransferHibernate h = s.find(LeadTransferHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        });
    }

    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            LeadTransferHibernate h = s.find(LeadTransferHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    @Override public List<LeadTransfer> findAllByLeadId(Long leadId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM LeadTransferHibernate WHERE leadId = :lid", LeadTransferHibernate.class)
                    .setParameter("lid", leadId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }
}
