package vn.com.be_crm.infrastructure.order.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.order.entity.OrderDeliveryTracking;
import vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository;
import vn.com.be_crm.infrastructure.order.entity.OrderDeliveryTrackingHibernate;
import vn.com.be_crm.infrastructure.order.mapper.OrderDeliveryTrackingHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IOrderDeliveryTrackingRepository.
 */
@Repository
public class OrderDeliveryTrackingRepositoryImpl implements IOrderDeliveryTrackingRepository {
    private final SessionFactory sf;
    private final OrderDeliveryTrackingHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public OrderDeliveryTrackingRepositoryImpl(SessionFactory sf, OrderDeliveryTrackingHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật OrderDeliveryTracking. @param t @return entity sau khi lưu */
    @Override public OrderDeliveryTracking save(OrderDeliveryTracking t) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderDeliveryTrackingHibernate m = s.merge(mapper.toHibernate(t));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm OrderDeliveryTracking theo ID. @param id @return Optional */
    @Override public Optional<OrderDeliveryTracking> findById(Long id) {
        try (Session s = sf.openSession()) {
            OrderDeliveryTrackingHibernate h = s.find(OrderDeliveryTrackingHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa OrderDeliveryTracking. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderDeliveryTrackingHibernate h = s.find(OrderDeliveryTrackingHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách OrderDeliveryTracking theo orderId. @param orderId @return danh sách */
    @Override public List<OrderDeliveryTracking> findAllByOrderId(Long orderId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM OrderDeliveryTrackingHibernate WHERE orderId = :oid", OrderDeliveryTrackingHibernate.class)
                    .setParameter("oid", orderId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
