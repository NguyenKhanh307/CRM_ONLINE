package vn.com.be_crm.infrastructure.opportunity.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.infrastructure.opportunity.entity.OpportunityItemHibernate;
import vn.com.be_crm.infrastructure.opportunity.mapper.OpportunityItemHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Hibernate implementation của IOpportunityItemRepository.
 */
@Repository
public class OpportunityItemRepositoryImpl implements IOpportunityItemRepository {
    private final SessionFactory sf;
    private final OpportunityItemHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public OpportunityItemRepositoryImpl(SessionFactory sf, OpportunityItemHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật OpportunityItem. @param i domain entity @return entity sau khi lưu */
    @Override public OpportunityItem save(OpportunityItem i) {
        return TxSupport.write(sf, s -> {
            OpportunityItemHibernate m = s.merge(mapper.toHibernate(i));
            return mapper.toDomain(m);
        });
    }

    /** Tìm OpportunityItem theo ID. @param id ID @return Optional */
    @Override public Optional<OpportunityItem> findById(Long id) {
        return TxSupport.read(sf, s -> {
            OpportunityItemHibernate h = s.find(OpportunityItemHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        });
    }

    /** Xóa OpportunityItem theo ID. @param id ID */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            OpportunityItemHibernate h = s.find(OpportunityItemHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /** Lấy danh sách OpportunityItem theo opportunityId. @param opportunityId ID @return danh sách */
    @Override public List<OpportunityItem> findAllByOpportunityId(Long opportunityId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM OpportunityItemHibernate WHERE opportunityId = :oid", OpportunityItemHibernate.class)
                    .setParameter("oid", opportunityId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }
}
