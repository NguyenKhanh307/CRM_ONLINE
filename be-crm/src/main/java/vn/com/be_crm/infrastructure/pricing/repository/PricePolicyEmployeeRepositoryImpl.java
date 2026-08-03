package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.pricing.entity.PricePolicyEmployee;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyEmployeeHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicySubEntityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

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
        return TxSupport.write(sf, s -> {
            PricePolicyEmployeeHibernate m = s.merge(mapper.toEmployeeHibernate(p));
            return mapper.toEmployeeDomain(m);
        });
    }

    /** Tìm PricePolicyEmployee theo ID. @param id @return Optional */
    @Override public Optional<PricePolicyEmployee> findById(Long id) {
        return TxSupport.read(sf, s -> {
            PricePolicyEmployeeHibernate h = s.find(PricePolicyEmployeeHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toEmployeeDomain);
        });
    }

    /** Xóa PricePolicyEmployee. @param id */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            PricePolicyEmployeeHibernate h = s.find(PricePolicyEmployeeHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /** Lấy danh sách PricePolicyEmployee theo pricePolicyId. @param pricePolicyId @return danh sách */
    @Override public List<PricePolicyEmployee> findAllByPricePolicyId(Long pricePolicyId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM PricePolicyEmployeeHibernate WHERE pricePolicyId = :pid", PricePolicyEmployeeHibernate.class)
                    .setParameter("pid", pricePolicyId).list().stream().map(mapper::toEmployeeDomain).collect(Collectors.toList());
        });
    }

    /** ID các chính sách userId được phép dùng: không có dòng nào (không giới hạn) HOẶC có dòng khớp userId. @param userId @return danh sách policy ID */
    @Override public List<Long> findEligiblePolicyIdsForUser(Long userId) {
        return TxSupport.read(sf, s -> s.createQuery(
                "SELECT DISTINCT pp.id FROM PricePolicyHibernate pp WHERE " +
                "NOT EXISTS (SELECT 1 FROM PricePolicyEmployeeHibernate e WHERE e.pricePolicyId = pp.id) " +
                "OR EXISTS (SELECT 1 FROM PricePolicyEmployeeHibernate e2 WHERE e2.pricePolicyId = pp.id AND e2.userId = :uid)",
                Long.class).setParameter("uid", userId).list());
    }

    /** true nếu policy không giới hạn nhân viên (0 dòng) HOẶC có dòng khớp userId. @param pricePolicyId @param userId @return có hợp lệ không */
    @Override public boolean isEligibleForUser(Long pricePolicyId, Long userId) {
        return TxSupport.read(sf, s -> {
            Long count = s.createQuery("SELECT COUNT(e) FROM PricePolicyEmployeeHibernate e WHERE e.pricePolicyId = :pid", Long.class)
                    .setParameter("pid", pricePolicyId).uniqueResult();
            if (count == null || count == 0) return true;
            Long match = s.createQuery("SELECT COUNT(e) FROM PricePolicyEmployeeHibernate e WHERE e.pricePolicyId = :pid AND e.userId = :uid", Long.class)
                    .setParameter("pid", pricePolicyId).setParameter("uid", userId).uniqueResult();
            return match != null && match > 0;
        });
    }
}
