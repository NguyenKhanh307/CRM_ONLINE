package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.pricing.entity.PricePolicyCustomerCategory;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerCategoryRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyCustomerCategoryHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicySubEntityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Hibernate implementation của IPricePolicyCustomerCategoryRepository.
 */
@Repository
public class PricePolicyCustomerCategoryRepositoryImpl implements IPricePolicyCustomerCategoryRepository {
    private final SessionFactory sf;
    private final PricePolicySubEntityHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public PricePolicyCustomerCategoryRepositoryImpl(SessionFactory sf, PricePolicySubEntityHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu PricePolicyCustomerCategory. @param p @return entity sau khi lưu */
    @Override public PricePolicyCustomerCategory save(PricePolicyCustomerCategory p) {
        return TxSupport.write(sf, s -> {
            PricePolicyCustomerCategoryHibernate m = s.merge(mapper.toCategoryHibernate(p));
            return mapper.toCategoryDomain(m);
        });
    }

    /** Tìm PricePolicyCustomerCategory theo ID. @param id @return Optional */
    @Override public Optional<PricePolicyCustomerCategory> findById(Long id) {
        return TxSupport.read(sf, s -> {
            PricePolicyCustomerCategoryHibernate h = s.find(PricePolicyCustomerCategoryHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toCategoryDomain);
        });
    }

    /** Xóa PricePolicyCustomerCategory. @param id */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            PricePolicyCustomerCategoryHibernate h = s.find(PricePolicyCustomerCategoryHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /** Lấy danh sách PricePolicyCustomerCategory theo pricePolicyId. @param pricePolicyId @return danh sách */
    @Override public List<PricePolicyCustomerCategory> findAllByPricePolicyId(Long pricePolicyId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM PricePolicyCustomerCategoryHibernate WHERE pricePolicyId = :pid", PricePolicyCustomerCategoryHibernate.class)
                    .setParameter("pid", pricePolicyId).list().stream().map(mapper::toCategoryDomain).collect(Collectors.toList());
        });
    }
}
