package vn.com.be_crm.infrastructure.customer.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.customer.entity.InventoryCheck;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository;
import vn.com.be_crm.infrastructure.customer.entity.InventoryCheckHibernate;
import vn.com.be_crm.infrastructure.customer.mapper.InventoryCheckHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IInventoryCheckRepository.
 */
@Repository
public class InventoryCheckRepositoryImpl implements IInventoryCheckRepository {
    private final SessionFactory sf;
    private final InventoryCheckHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public InventoryCheckRepositoryImpl(SessionFactory sf, InventoryCheckHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật InventoryCheck.
     * @param check domain entity @return entity sau khi lưu
     */
    @Override
    public InventoryCheck save(InventoryCheck check) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InventoryCheckHibernate m = s.merge(mapper.toHibernate(check));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /**
     * Tìm InventoryCheck theo ID.
     * @param id ID @return Optional
     */
    @Override
    public Optional<InventoryCheck> findById(Long id) {
        try (Session s = sf.openSession()) {
            InventoryCheckHibernate h = s.find(InventoryCheckHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /**
     * Xóa InventoryCheck theo ID.
     * @param id ID cần xóa
     */
    @Override
    public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InventoryCheckHibernate h = s.find(InventoryCheckHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /**
     * Lấy danh sách InventoryCheck có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<InventoryCheck> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<InventoryCheck> items = s.createQuery(
                    "FROM InventoryCheckHibernate ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    InventoryCheckHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(i) FROM InventoryCheckHibernate i", Long.class).uniqueResult();
            return PageResult.<InventoryCheck>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
