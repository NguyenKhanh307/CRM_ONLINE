package vn.com.be_crm.infrastructure.customer.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.customer.entity.InventoryCheckItem;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository;
import vn.com.be_crm.infrastructure.customer.entity.InventoryCheckItemHibernate;
import vn.com.be_crm.infrastructure.customer.mapper.InventoryCheckItemHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IInventoryCheckItemRepository.
 */
@Repository
public class InventoryCheckItemRepositoryImpl implements IInventoryCheckItemRepository {
    private final SessionFactory sf;
    private final InventoryCheckItemHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public InventoryCheckItemRepositoryImpl(SessionFactory sf, InventoryCheckItemHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật InventoryCheckItem.
     * @param item domain entity @return entity sau khi lưu
     */
    @Override
    public InventoryCheckItem save(InventoryCheckItem item) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InventoryCheckItemHibernate m = s.merge(mapper.toHibernate(item));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /**
     * Tìm InventoryCheckItem theo ID.
     * @param id ID @return Optional
     */
    @Override
    public Optional<InventoryCheckItem> findById(Long id) {
        try (Session s = sf.openSession()) {
            InventoryCheckItemHibernate h = s.find(InventoryCheckItemHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /**
     * Xóa InventoryCheckItem theo ID.
     * @param id ID cần xóa
     */
    @Override
    public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InventoryCheckItemHibernate h = s.find(InventoryCheckItemHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /**
     * Lấy danh sách InventoryCheckItem theo checkId.
     * @param checkId ID phiếu kiểm kho @return danh sách
     */
    @Override
    public List<InventoryCheckItem> findAllByCheckId(Long checkId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM InventoryCheckItemHibernate WHERE checkId = :cid", InventoryCheckItemHibernate.class)
                    .setParameter("cid", checkId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
