package vn.com.be_crm.infrastructure.pricing.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;
import vn.com.be_crm.infrastructure.pricing.entity.PricePolicyHibernate;
import vn.com.be_crm.infrastructure.pricing.mapper.PricePolicyHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PricePolicyHibernate m = s.merge(mapper.toHibernate(p));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm PricePolicy theo ID. @param id @return Optional */
    @Override public Optional<PricePolicy> findById(Long id) {
        try (Session s = sf.openSession()) {
            PricePolicyHibernate h = s.find(PricePolicyHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa PricePolicy. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            PricePolicyHibernate h = s.find(PricePolicyHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách PricePolicy có phân trang. @param r @return PageResult */
    @Override public PageResult<PricePolicy> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<PricePolicy> items = s.createQuery(
                    "FROM PricePolicyHibernate ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    PricePolicyHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(p) FROM PricePolicyHibernate p", Long.class).uniqueResult();
            return PageResult.<PricePolicy>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
