package vn.com.be_crm.infrastructure.product.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.infrastructure.product.entity.ProductHibernate;
import vn.com.be_crm.infrastructure.product.mapper.ProductHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IProductRepository.
 * Soft delete: deleteById set deleted_at = now().
 */
@Repository
public class ProductRepositoryImpl implements IProductRepository {
    private final SessionFactory sf;
    private final ProductHibernateMapper mapper;
    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public ProductRepositoryImpl(SessionFactory sf, ProductHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }
    /**
     * Lưu mới hoặc cập nhật Product.
     * @param p domain entity @return entity sau khi lưu
     */
    @Override public Product save(Product p) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ProductHibernate m = s.merge(mapper.toHibernate(p));
            tx.commit(); return mapper.toDomain(m);
        }
    }
    /**
     * Tìm Product theo ID — chỉ trả về nếu chưa xóa mềm.
     * @param id ID @return Optional
     */
    @Override public Optional<Product> findById(Long id) {
        try (Session s = sf.openSession()) {
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }
    /**
     * Xóa mềm Product bằng cách set deleted_at = now().
     * @param id ID cần xóa mềm
     */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ProductHibernate h = s.find(ProductHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); s.merge(h); }
            tx.commit();
        }
    }
    /**
     * Lấy danh sách Product chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override public PageResult<Product> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<Product> items = s.createQuery("FROM ProductHibernate WHERE deletedAt IS NULL ORDER BY " + r.getSortBy() + " " + r.getSortDir(), ProductHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(p) FROM ProductHibernate p WHERE p.deletedAt IS NULL", Long.class).uniqueResult();
            return PageResult.<Product>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
