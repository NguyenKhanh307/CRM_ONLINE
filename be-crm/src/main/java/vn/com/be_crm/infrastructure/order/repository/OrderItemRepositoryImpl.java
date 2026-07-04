package vn.com.be_crm.infrastructure.order.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.infrastructure.order.entity.OrderItemHibernate;
import vn.com.be_crm.infrastructure.order.mapper.OrderItemHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IOrderItemRepository.
 */
@Repository
public class OrderItemRepositoryImpl implements IOrderItemRepository {
    private final SessionFactory sf;
    private final OrderItemHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public OrderItemRepositoryImpl(SessionFactory sf, OrderItemHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật OrderItem. @param i @return entity sau khi lưu */
    @Override public OrderItem save(OrderItem i) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderItemHibernate m = s.merge(mapper.toHibernate(i));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm OrderItem theo ID. @param id @return Optional */
    @Override public Optional<OrderItem> findById(Long id) {
        try (Session s = sf.openSession()) {
            OrderItemHibernate h = s.find(OrderItemHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa OrderItem. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderItemHibernate h = s.find(OrderItemHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách OrderItem theo orderId. @param orderId @return danh sách */
    @Override public List<OrderItem> findAllByOrderId(Long orderId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM OrderItemHibernate WHERE orderId = :oid", OrderItemHibernate.class)
                    .setParameter("oid", orderId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
