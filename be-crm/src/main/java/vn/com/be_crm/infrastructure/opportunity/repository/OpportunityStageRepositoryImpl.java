package vn.com.be_crm.infrastructure.opportunity.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;
import vn.com.be_crm.infrastructure.opportunity.entity.OpportunityStageHibernate;
import vn.com.be_crm.infrastructure.opportunity.mapper.OpportunityStageHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/** Hibernate implementation của IOpportunityStageRepository. */
@Repository
public class OpportunityStageRepositoryImpl implements IOpportunityStageRepository {
    private final SessionFactory sf;
    private final OpportunityStageHibernateMapper mapper;
    /**
     * @param sf SessionFactory @param mapper mapper
     */
    public OpportunityStageRepositoryImpl(SessionFactory sf, OpportunityStageHibernateMapper mapper) { this.sf = sf; this.mapper = mapper; }
    /** @param stage entity @return saved */
    @Override public OpportunityStage save(OpportunityStage stage) {
        return TxSupport.write(sf, s -> {
            OpportunityStageHibernate m = s.merge(mapper.toHibernate(stage));
            return mapper.toDomain(m);
        });
    }
    /** @param id ID @return Optional */
    @Override public Optional<OpportunityStage> findById(Long id) {
        return TxSupport.read(sf, s -> {
            return Optional.ofNullable(s.find(OpportunityStageHibernate.class, id)).map(mapper::toDomain);
        });
    }
    /** @param id ID to delete */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            OpportunityStageHibernate h = s.find(OpportunityStageHibernate.class, id);
            if (h != null) s.remove(h); });
    }
    /** @param r page request @return PageResult */
    @Override public PageResult<OpportunityStage> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            List<OpportunityStage> items = s.createQuery(
                    "FROM OpportunityStageHibernate ORDER BY " + r.getSortBy() + " " + r.getSortDir(), OpportunityStageHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(s) FROM OpportunityStageHibernate s", Long.class).uniqueResult();
            return PageResult.<OpportunityStage>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }
}
