package vn.com.be_crm.infrastructure.opportunity.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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

/** Hibernate implementation của IOpportunityStageRepository. */
@Repository
public class OpportunityStageRepositoryImpl implements IOpportunityStageRepository {
    private final SessionFactory sf;
    private final OpportunityStageHibernateMapper mapper;
    /**
     * @param sf SessionFactory @param mapper mapper
     */
    public OpportunityStageRepositoryImpl(SessionFactory sf, OpportunityStageHibernateMapper mapper) { this.sf = sf; this.mapper = mapper; }
    /** @param s entity @return saved */
    @Override public OpportunityStage save(OpportunityStage s) {
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            OpportunityStageHibernate m = session.merge(mapper.toHibernate(s));
            tx.commit(); return mapper.toDomain(m);
        }
    }
    /** @param id ID @return Optional */
    @Override public Optional<OpportunityStage> findById(Long id) {
        try (Session session = sf.openSession()) {
            return Optional.ofNullable(session.find(OpportunityStageHibernate.class, id)).map(mapper::toDomain);
        }
    }
    /** @param id ID to delete */
    @Override public void deleteById(Long id) {
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            OpportunityStageHibernate h = session.find(OpportunityStageHibernate.class, id);
            if (h != null) session.remove(h); tx.commit();
        }
    }
    /** @param r page request @return PageResult */
    @Override public PageResult<OpportunityStage> findAll(PageRequest r) {
        try (Session session = sf.openSession()) {
            List<OpportunityStage> items = session.createQuery(
                    "FROM OpportunityStageHibernate ORDER BY " + r.getSortBy() + " " + r.getSortDir(), OpportunityStageHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = session.createQuery("SELECT COUNT(s) FROM OpportunityStageHibernate s", Long.class).uniqueResult();
            return PageResult.<OpportunityStage>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
