package vn.com.be_crm.infrastructure.order.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.infrastructure.order.entity.OrderHibernate;
import vn.com.be_crm.infrastructure.order.mapper.OrderHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IOrderRepository.
 */
@Repository
public class OrderRepositoryImpl implements IOrderRepository {
    private final SessionFactory sf;
    private final OrderHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public OrderRepositoryImpl(SessionFactory sf, OrderHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật Order. @param o @return entity sau khi lưu */
    @Override public Order save(Order o) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderHibernate m = s.merge(mapper.toHibernate(o));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Order theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Order> findById(Long id) {
        try (Session s = sf.openSession()) {
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Order. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OrderHibernate h = s.find(OrderHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Order chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Order> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<Order> items = s.createQuery(
                    "FROM OrderHibernate WHERE deletedAt IS NULL ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    OrderHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(o) FROM OrderHibernate o WHERE o.deletedAt IS NULL", Long.class).uniqueResult();
            return PageResult.<Order>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
