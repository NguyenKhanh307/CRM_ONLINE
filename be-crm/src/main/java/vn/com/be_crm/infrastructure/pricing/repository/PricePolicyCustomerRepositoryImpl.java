package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.pricing.entity.PricePolicyCustomer;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyCustomerHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicySubEntityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IPricePolicyCustomerRepository.
 */
@Repository
public class PricePolicyCustomerRepositoryImpl implements IPricePolicyCustomerRepository {
    private final SessionFactory sf;
    private final PricePolicySubEntityHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public PricePolicyCustomerRepositoryImpl(SessionFactory sf, PricePolicySubEntityHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu PricePolicyCustomer. @param p @return entity sau khi lưu */
    @Override public PricePolicyCustomer save(PricePolicyCustomer p) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PricePolicyCustomerHibernate m = s.merge(mapper.toCustomerHibernate(p));
            tx.commit(); return mapper.toCustomerDomain(m);
        }
    }

    /** Tìm PricePolicyCustomer theo ID. @param id @return Optional */
    @Override public Optional<PricePolicyCustomer> findById(Long id) {
        try (Session s = sf.openSession()) {
            PricePolicyCustomerHibernate h = s.find(PricePolicyCustomerHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toCustomerDomain);
        }
    }

    /** Xóa PricePolicyCustomer. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PricePolicyCustomerHibernate h = s.find(PricePolicyCustomerHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách PricePolicyCustomer theo pricePolicyId. @param pricePolicyId @return danh sách */
    @Override public List<PricePolicyCustomer> findAllByPricePolicyId(Long pricePolicyId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM PricePolicyCustomerHibernate WHERE pricePolicyId = :pid", PricePolicyCustomerHibernate.class)
                    .setParameter("pid", pricePolicyId).list().stream().map(mapper::toCustomerDomain).collect(Collectors.toList());
        }
    }
}
