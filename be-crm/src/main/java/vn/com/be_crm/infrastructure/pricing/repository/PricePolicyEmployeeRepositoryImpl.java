package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.pricing.entity.PricePolicyEmployee;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyEmployeeHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicySubEntityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IPricePolicyEmployeeRepository.
 */
@Repository
public class PricePolicyEmployeeRepositoryImpl implements IPricePolicyEmployeeRepository {
    private final SessionFactory sf;
    private final PricePolicySubEntityHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public PricePolicyEmployeeRepositoryImpl(SessionFactory sf, PricePolicySubEntityHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu PricePolicyEmployee. @param p @return entity sau khi lưu */
    @Override public PricePolicyEmployee save(PricePolicyEmployee p) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PricePolicyEmployeeHibernate m = s.merge(mapper.toEmployeeHibernate(p));
            tx.commit(); return mapper.toEmployeeDomain(m);
        }
    }

    /** Tìm PricePolicyEmployee theo ID. @param id @return Optional */
    @Override public Optional<PricePolicyEmployee> findById(Long id) {
        try (Session s = sf.openSession()) {
            PricePolicyEmployeeHibernate h = s.find(PricePolicyEmployeeHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toEmployeeDomain);
        }
    }

    /** Xóa PricePolicyEmployee. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PricePolicyEmployeeHibernate h = s.find(PricePolicyEmployeeHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách PricePolicyEmployee theo pricePolicyId. @param pricePolicyId @return danh sách */
    @Override public List<PricePolicyEmployee> findAllByPricePolicyId(Long pricePolicyId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM PricePolicyEmployeeHibernate WHERE pricePolicyId = :pid", PricePolicyEmployeeHibernate.class)
                    .setParameter("pid", pricePolicyId).list().stream().map(mapper::toEmployeeDomain).collect(Collectors.toList());
        }
    }
}
