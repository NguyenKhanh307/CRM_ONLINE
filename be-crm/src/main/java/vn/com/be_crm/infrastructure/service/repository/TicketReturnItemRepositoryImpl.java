package vn.com.be_crm.infrastructure.service.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;
import vn.com.be_crm.infrastructure.service.entity.TicketReturnItemHibernate;
import vn.com.be_crm.infrastructure.service.mapper.TicketReturnItemHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của ITicketReturnItemRepository.
 */
@Repository
public class TicketReturnItemRepositoryImpl implements ITicketReturnItemRepository {
    private final SessionFactory sf;
    private final TicketReturnItemHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public TicketReturnItemRepositoryImpl(SessionFactory sf, TicketReturnItemHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật dòng hàng. @param i @return entity sau khi lưu */
    @Override public TicketReturnItem save(TicketReturnItem i) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            TicketReturnItemHibernate m = s.merge(mapper.toHibernate(i));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm dòng hàng theo ID. @param id @return Optional */
    @Override public Optional<TicketReturnItem> findById(Long id) {
        try (Session s = sf.openSession()) {
            TicketReturnItemHibernate h = s.find(TicketReturnItemHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa dòng hàng theo ID. @param id */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            TicketReturnItemHibernate h = s.find(TicketReturnItemHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách dòng hàng theo ticketId. @param ticketId @return danh sách */
    @Override public List<TicketReturnItem> findAllByTicketId(Long ticketId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM TicketReturnItemHibernate WHERE ticketId = :tid ORDER BY id", TicketReturnItemHibernate.class)
                    .setParameter("tid", ticketId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
