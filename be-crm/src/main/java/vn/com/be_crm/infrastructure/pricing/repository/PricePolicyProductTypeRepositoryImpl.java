package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProductType;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductTypeRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyProductTypeHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicySubEntityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IPricePolicyProductTypeRepository.
 */
@Repository
public class PricePolicyProductTypeRepositoryImpl implements IPricePolicyProductTypeRepository {
    private final SessionFactory sf;
    private final PricePolicySubEntityHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public PricePolicyProductTypeRepositoryImpl(SessionFactory sf, PricePolicySubEntityHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu PricePolicyProductType. @param p @return entity sau khi lưu */
    @Override public PricePolicyProductType save(PricePolicyProductType p) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PricePolicyProductTypeHibernate m = s.merge(mapper.toProductTypeHibernate(p));
            tx.commit(); return mapper.toProductTypeDomain(m);
        }
    }

    /** Tìm PricePolicyProductType theo ID. @param id @return Optional */
    @Override public Optional<PricePolicyProductType> findById(Long id) {
        try (Session s = sf.openSession()) {
            PricePolicyProductTypeHibernate h = s.find(PricePolicyProductTypeHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toProductTypeDomain);
        }
    }

    /** Xóa PricePolicyProductType. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PricePolicyProductTypeHibernate h = s.find(PricePolicyProductTypeHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách PricePolicyProductType theo pricePolicyId. @param pricePolicyId @return danh sách */
    @Override public List<PricePolicyProductType> findAllByPricePolicyId(Long pricePolicyId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM PricePolicyProductTypeHibernate WHERE pricePolicyId = :pid", PricePolicyProductTypeHibernate.class)
                    .setParameter("pid", pricePolicyId).list().stream().map(mapper::toProductTypeDomain).collect(Collectors.toList());
        }
    }
}
