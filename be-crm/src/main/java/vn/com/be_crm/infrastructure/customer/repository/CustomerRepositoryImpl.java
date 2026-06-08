package vn.com.be_crm.infrastructure.customer.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.infrastructure.customer.entity.CustomerHibernate;
import vn.com.be_crm.infrastructure.customer.mapper.CustomerHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của ICustomerRepository.
 * Soft delete: deleteById set deleted_at = now().
 */
@Repository
public class CustomerRepositoryImpl implements ICustomerRepository {
    private final SessionFactory sf;
    private final CustomerHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public CustomerRepositoryImpl(SessionFactory sf, CustomerHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật Customer.
     * @param c domain entity @return entity sau khi lưu
     */
    @Override
    public Customer save(Customer c) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            CustomerHibernate m = s.merge(mapper.toHibernate(c));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /**
     * Tìm Customer theo ID — chỉ trả về nếu chưa xóa mềm.
     * @param id ID @return Optional
     */
    @Override
    public Optional<Customer> findById(Long id) {
        try (Session s = sf.openSession()) {
            CustomerHibernate h = s.find(CustomerHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /**
     * Xóa mềm Customer bằng cách set deleted_at = now().
     * @param id ID cần xóa mềm
     */
    @Override
    public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            CustomerHibernate h = s.find(CustomerHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); s.merge(h); }
            tx.commit();
        }
    }

    /**
     * Lấy danh sách Customer chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<Customer> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<Customer> items = s.createQuery(
                    "FROM CustomerHibernate WHERE deletedAt IS NULL ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    CustomerHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(c) FROM CustomerHibernate c WHERE c.deletedAt IS NULL", Long.class).uniqueResult();
            return PageResult.<Customer>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
