package vn.com.be_crm.infrastructure.notification.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.notification.entity.Notification;
import vn.com.be_crm.domain.notification.repository.INotificationRepository;
import vn.com.be_crm.infrastructure.notification.entity.NotificationHibernate;
import vn.com.be_crm.infrastructure.notification.mapper.NotificationHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của INotificationRepository.
 */
@Repository
public class NotificationRepositoryImpl implements INotificationRepository {
    private final SessionFactory sf;
    private final NotificationHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public NotificationRepositoryImpl(SessionFactory sf, NotificationHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu một thông báo. @param n domain entity @return entity sau khi lưu */
    @Override public Notification save(Notification n) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            NotificationHibernate m = s.merge(mapper.toHibernate(n));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Lấy danh sách thông báo của người nhận, mới nhất trước. @param recipientUserId ID @param limit số tối đa */
    @Override public List<Notification> findByRecipient(Long recipientUserId, int limit) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                    "FROM NotificationHibernate WHERE recipientUserId = :uid ORDER BY createdAt DESC",
                    NotificationHibernate.class)
                    .setParameter("uid", recipientUserId).setMaxResults(limit).list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }

    /** Đếm số thông báo chưa đọc. @param recipientUserId ID @return số lượng */
    @Override public long countUnread(Long recipientUserId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                    "SELECT COUNT(n) FROM NotificationHibernate n WHERE n.recipientUserId = :uid AND n.isRead = false",
                    Long.class).setParameter("uid", recipientUserId).uniqueResult();
        }
    }

    /** Đánh dấu một thông báo đã đọc (đúng người nhận). @param id ID @param recipientUserId ID người nhận */
    @Override public void markRead(Long id, Long recipientUserId) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.createMutationQuery(
                    "UPDATE NotificationHibernate SET isRead = true WHERE id = :id AND recipientUserId = :uid")
                    .setParameter("id", id).setParameter("uid", recipientUserId).executeUpdate();
            tx.commit();
        }
    }

    /** Đánh dấu tất cả thông báo của người nhận đã đọc. @param recipientUserId ID người nhận */
    @Override public void markAllRead(Long recipientUserId) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.createMutationQuery(
                    "UPDATE NotificationHibernate SET isRead = true WHERE recipientUserId = :uid AND isRead = false")
                    .setParameter("uid", recipientUserId).executeUpdate();
            tx.commit();
        }
    }
}
