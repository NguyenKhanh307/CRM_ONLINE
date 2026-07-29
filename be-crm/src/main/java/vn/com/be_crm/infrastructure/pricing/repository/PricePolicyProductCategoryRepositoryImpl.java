package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProductCategory;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductCategoryRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyProductCategoryHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicySubEntityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Hibernate implementation của IPricePolicyProductCategoryRepository.
 */
@Repository
public class PricePolicyProductCategoryRepositoryImpl implements IPricePolicyProductCategoryRepository {
    private final SessionFactory sf;
    private final PricePolicySubEntityHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public PricePolicyProductCategoryRepositoryImpl(SessionFactory sf, PricePolicySubEntityHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu PricePolicyProductCategory. @param p @return entity sau khi lưu */
    @Override public PricePolicyProductCategory save(PricePolicyProductCategory p) {
        return TxSupport.write(sf, s -> {
            PricePolicyProductCategoryHibernate m = s.merge(mapper.toCategoryHibernate(p));
            return mapper.toCategoryDomain(m);
        });
    }

    /** Tìm PricePolicyProductCategory theo ID. @param id @return Optional */
    @Override public Optional<PricePolicyProductCategory> findById(Long id) {
        return TxSupport.read(sf, s -> {
            PricePolicyProductCategoryHibernate h = s.find(PricePolicyProductCategoryHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toCategoryDomain);
        });
    }

    /** Xóa PricePolicyProductCategory. @param id */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            PricePolicyProductCategoryHibernate h = s.find(PricePolicyProductCategoryHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /** Lấy danh sách PricePolicyProductCategory theo pricePolicyId. @param pricePolicyId @return danh sách */
    @Override public List<PricePolicyProductCategory> findAllByPricePolicyId(Long pricePolicyId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM PricePolicyProductCategoryHibernate WHERE pricePolicyId = :pid", PricePolicyProductCategoryHibernate.class)
                    .setParameter("pid", pricePolicyId).list().stream().map(mapper::toCategoryDomain).collect(Collectors.toList());
        });
    }
}
