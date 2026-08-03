package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProduct;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyProductHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicySubEntityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IPricePolicyProductRepository.
 */
@Repository
public class PricePolicyProductRepositoryImpl implements IPricePolicyProductRepository {
    private final SessionFactory sf;
    private final PricePolicySubEntityHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public PricePolicyProductRepositoryImpl(SessionFactory sf, PricePolicySubEntityHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu PricePolicyProduct. @param p @return entity sau khi lưu */
    @Override public PricePolicyProduct save(PricePolicyProduct p) {
        return TxSupport.write(sf, s -> {
            PricePolicyProductHibernate m = s.merge(mapper.toProductHibernate(p));
            return mapper.toProductDomain(m);
        });
    }

    /** Tìm PricePolicyProduct theo ID. @param id @return Optional */
    @Override public Optional<PricePolicyProduct> findById(Long id) {
        return TxSupport.read(sf, s -> {
            PricePolicyProductHibernate h = s.find(PricePolicyProductHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toProductDomain);
        });
    }

    /** Xóa PricePolicyProduct. @param id */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            PricePolicyProductHibernate h = s.find(PricePolicyProductHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /** Lấy danh sách PricePolicyProduct theo pricePolicyId. @param pricePolicyId @return danh sách */
    @Override public List<PricePolicyProduct> findAllByPricePolicyId(Long pricePolicyId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM PricePolicyProductHibernate WHERE pricePolicyId = :pid", PricePolicyProductHibernate.class)
                    .setParameter("pid", pricePolicyId).list().stream().map(mapper::toProductDomain).collect(Collectors.toList());
        });
    }
}
