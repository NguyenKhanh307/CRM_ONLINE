package vn.com.be_crm.infrastructure.order.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.order.entity.OrderRevenueRecord;
import vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository;
import vn.com.be_crm.infrastructure.order.entity.OrderRevenueRecordHibernate;
import vn.com.be_crm.infrastructure.order.mapper.OrderRevenueRecordHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IOrderRevenueRecordRepository.
 */
@Repository
public class OrderRevenueRecordRepositoryImpl implements IOrderRevenueRecordRepository {
    private final SessionFactory sf;
    private final OrderRevenueRecordHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public OrderRevenueRecordRepositoryImpl(SessionFactory sf, OrderRevenueRecordHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu bản ghi doanh thu. @param r @return entity sau khi lưu */
    @Override public OrderRevenueRecord save(OrderRevenueRecord r) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderRevenueRecordHibernate m = s.merge(mapper.toHibernate(r));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm OrderRevenueRecord theo ID. @param id @return Optional */
    @Override public Optional<OrderRevenueRecord> findById(Long id) {
        try (Session s = sf.openSession()) {
            OrderRevenueRecordHibernate h = s.find(OrderRevenueRecordHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa OrderRevenueRecord. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderRevenueRecordHibernate h = s.find(OrderRevenueRecordHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách bản ghi doanh thu theo orderId. @param orderId @return danh sách */
    @Override public List<OrderRevenueRecord> findAllByOrderId(Long orderId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM OrderRevenueRecordHibernate WHERE orderId = :oid", OrderRevenueRecordHibernate.class)
                    .setParameter("oid", orderId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
