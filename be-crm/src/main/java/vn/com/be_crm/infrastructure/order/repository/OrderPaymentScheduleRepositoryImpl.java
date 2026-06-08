package vn.com.be_crm.infrastructure.order.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.order.entity.OrderPaymentSchedule;
import vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository;
import vn.com.be_crm.infrastructure.order.entity.OrderPaymentScheduleHibernate;
import vn.com.be_crm.infrastructure.order.mapper.OrderPaymentScheduleHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IOrderPaymentScheduleRepository.
 */
@Repository
public class OrderPaymentScheduleRepositoryImpl implements IOrderPaymentScheduleRepository {
    private final SessionFactory sf;
    private final OrderPaymentScheduleHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public OrderPaymentScheduleRepositoryImpl(SessionFactory sf, OrderPaymentScheduleHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật OrderPaymentSchedule. @param ps @return entity sau khi lưu */
    @Override public OrderPaymentSchedule save(OrderPaymentSchedule ps) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderPaymentScheduleHibernate m = s.merge(mapper.toHibernate(ps));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm OrderPaymentSchedule theo ID. @param id @return Optional */
    @Override public Optional<OrderPaymentSchedule> findById(Long id) {
        try (Session s = sf.openSession()) {
            OrderPaymentScheduleHibernate h = s.find(OrderPaymentScheduleHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa OrderPaymentSchedule. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderPaymentScheduleHibernate h = s.find(OrderPaymentScheduleHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách OrderPaymentSchedule theo orderId. @param orderId @return danh sách */
    @Override public List<OrderPaymentSchedule> findAllByOrderId(Long orderId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM OrderPaymentScheduleHibernate WHERE orderId = :oid", OrderPaymentScheduleHibernate.class)
                    .setParameter("oid", orderId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
