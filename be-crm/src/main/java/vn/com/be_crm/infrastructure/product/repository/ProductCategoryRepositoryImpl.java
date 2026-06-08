package vn.com.be_crm.infrastructure.product.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.product.entity.ProductCategory;
import vn.com.be_crm.domain.product.repository.IProductCategoryRepository;
import vn.com.be_crm.infrastructure.product.entity.ProductCategoryHibernate;
import vn.com.be_crm.infrastructure.product.mapper.ProductCategoryHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Hibernate implementation của IProductCategoryRepository. */
@Repository
public class ProductCategoryRepositoryImpl implements IProductCategoryRepository {
    private final SessionFactory sf;
    private final ProductCategoryHibernateMapper mapper;
    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public ProductCategoryRepositoryImpl(SessionFactory sf, ProductCategoryHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }
    /**
     * Lưu mới hoặc cập nhật ProductCategory.
     * @param c domain entity @return entity sau khi lưu
     */
    @Override public ProductCategory save(ProductCategory c) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ProductCategoryHibernate m = s.merge(mapper.toHibernate(c));
            tx.commit(); return mapper.toDomain(m);
        }
    }
    /**
     * Tìm ProductCategory theo ID.
     * @param id ID @return Optional
     */
    @Override public Optional<ProductCategory> findById(Long id) {
        try (Session s = sf.openSession()) {
            return Optional.ofNullable(s.find(ProductCategoryHibernate.class, id)).map(mapper::toDomain);
        }
    }
    /**
     * Xóa ProductCategory theo ID.
     * @param id ID cần xóa
     */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ProductCategoryHibernate h = s.find(ProductCategoryHibernate.class, id);
            if (h != null) s.remove(h); tx.commit();
        }
    }
    /**
     * Lấy danh sách ProductCategory có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override public PageResult<ProductCategory> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<ProductCategory> items = s.createQuery("FROM ProductCategoryHibernate ORDER BY " + r.getSortBy() + " " + r.getSortDir(), ProductCategoryHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(c) FROM ProductCategoryHibernate c", Long.class).uniqueResult();
            return PageResult.<ProductCategory>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
