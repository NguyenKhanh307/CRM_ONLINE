package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.util.ListQueryUtils;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicyHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IPricePolicyRepository.
 */
@Repository
public class PricePolicyRepositoryImpl implements IPricePolicyRepository {
    private final SessionFactory sf;
    private final PricePolicyHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public PricePolicyRepositoryImpl(SessionFactory sf, PricePolicyHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật PricePolicy. @param p @return entity sau khi lưu */
    @Override public PricePolicy save(PricePolicy p) {
        return TxSupport.write(sf, s -> {
            PricePolicyHibernate m = s.merge(mapper.toHibernate(p));
            return mapper.toDomain(m);
        });
    }

    /** Tìm PricePolicy theo ID. @param id @return Optional */
    @Override public Optional<PricePolicy> findById(Long id) {
        return TxSupport.read(sf, s -> {
            PricePolicyHibernate h = s.find(PricePolicyHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        });
    }

    /** Tìm PricePolicy theo mã. @param code @return Optional */
    @Override public Optional<PricePolicy> findByCode(String code) {
        return TxSupport.read(sf, s -> s.createQuery(
                        "FROM PricePolicyHibernate WHERE code = :code", PricePolicyHibernate.class)
                .setParameter("code", code).uniqueResultOptional().map(mapper::toDomain));
    }

    /** Xóa PricePolicy. @param id */
    @Override public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            PricePolicyHibernate h = s.find(PricePolicyHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /** Lấy danh sách PricePolicy có phân trang, tìm theo mã/tên. @param r @return PageResult */
    @Override public PageResult<PricePolicy> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "code", "name");
            var listQuery = s.createQuery(
                    "FROM PricePolicyHibernate WHERE 1=1" + searchFilter
                            + " ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    PricePolicyHibernate.class);
            var countQuery = s.createQuery(
                    "SELECT COUNT(p) FROM PricePolicyHibernate p WHERE 1=1" + searchFilter, Long.class);
            if (!searchFilter.isEmpty()) {
                listQuery.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                countQuery.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
            }
            List<PricePolicy> items = listQuery
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = countQuery.uniqueResult();
            return PageResult.<PricePolicy>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }
}
