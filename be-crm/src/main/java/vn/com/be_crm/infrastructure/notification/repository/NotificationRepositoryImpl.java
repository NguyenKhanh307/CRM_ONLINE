package vn.com.be_crm.infrastructure.notification.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.notification.entity.Notification;
import vn.com.be_crm.domain.notification.entity.NotificationFeedItem;
import vn.com.be_crm.domain.notification.repository.INotificationRepository;
import vn.com.be_crm.infrastructure.notification.entity.NotificationHibernate;
import vn.com.be_crm.infrastructure.notification.entity.NotificationRecipientHibernate;
import vn.com.be_crm.infrastructure.notification.mapper.NotificationHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

// impl Hibernate của INotificationRepository. Không có quan hệ @ManyToOne giữa hai entity nên
// findByRecipient dùng HQL theta-join (2 entity không liên kết) qua notificationId = notification.id.
@Repository
public class NotificationRepositoryImpl implements INotificationRepository {
        private final SessionFactory sf;
        private final NotificationHibernateMapper mapper;

        public NotificationRepositoryImpl(SessionFactory sf, NotificationHibernateMapper mapper) {
                this.sf = sf;
                this.mapper = mapper;
        }

        // lưu 1 thông báo dùng chung + N dòng người nhận trong 1 transaction
        @Override
        public Notification save(Notification n, List<Long> recipientUserIds) {
                return TxSupport.write(sf, s -> {
                        NotificationHibernate saved = s.merge(mapper.toHibernate(n));
                        for (Long userId : recipientUserIds) {
                                NotificationRecipientHibernate r = new NotificationRecipientHibernate();
                                r.setNotificationId(saved.getId());
                                r.setRecipientUserId(userId);
                                r.setRead(false);
                                s.persist(r);
                        }
                        return mapper.toDomain(saved);
                });
        }

        // tìm các thông báo của 1 người nhận, sắp xếp mới nhất trước, giới hạn số lượng
        // trả về
        @Override
        public List<NotificationFeedItem> findByRecipient(Long recipientUserId, int limit) {
                return TxSupport.read(sf, s -> {
                        List<Object[]> rows = s.createQuery(
                                        "SELECT r.id, n.id, n.type, n.title, n.content, n.targetType, n.targetId, r.isRead, r.createdAt "
                                                        + "FROM NotificationRecipientHibernate r, NotificationHibernate n "
                                                        + "WHERE r.notificationId = n.id AND r.recipientUserId = :uid AND r.deletedAt IS NULL "
                                                        + "ORDER BY r.createdAt DESC",
                                        Object[].class)
                                        .setParameter("uid", recipientUserId).setMaxResults(limit).list();
                        return rows.stream().map(row -> new NotificationFeedItem(
                                        (Long) row[0], (Long) row[1], (String) row[2], (String) row[3], (String) row[4],
                                        (String) row[5], (Long) row[6], (Boolean) row[7], (LocalDateTime) row[8]))
                                        .collect(Collectors.toList());
                });
        }

        @Override
        public long countUnread(Long recipientUserId) {
                return TxSupport.read(sf, s -> s.createQuery(
                                "SELECT COUNT(r) FROM NotificationRecipientHibernate r WHERE r.recipientUserId = :uid"
                                                + " AND r.isRead = false AND r.deletedAt IS NULL",
                                Long.class).setParameter("uid", recipientUserId).uniqueResult());
        }

        // id = id dòng notification_recipients
        @Override
        public void markRead(Long id, Long recipientUserId) {
                TxSupport.writeVoid(sf, s -> {
                        s.createMutationQuery(
                                        "UPDATE NotificationRecipientHibernate SET isRead = true WHERE id = :id AND recipientUserId = :uid"
                                                        + " AND deletedAt IS NULL")
                                        .setParameter("id", id).setParameter("uid", recipientUserId).executeUpdate();
                });
        }

        @Override
        public void markAllRead(Long recipientUserId) {
                TxSupport.writeVoid(sf, s -> {
                        s.createMutationQuery(
                                        "UPDATE NotificationRecipientHibernate SET isRead = true WHERE recipientUserId = :uid AND isRead = false"
                                                        + " AND deletedAt IS NULL")
                                        .setParameter("uid", recipientUserId).executeUpdate();
                });
        }

        @Override
        public int softDeleteByIds(List<Long> ids, Long recipientUserId) {
                // HQL "IN ()" với danh sách rỗng sinh SQL không hợp lệ → chặn ngay từ đây
                if (ids == null || ids.isEmpty())
                        return 0;
                return TxSupport.write(sf, s -> s.createMutationQuery(
                                "UPDATE NotificationRecipientHibernate SET deletedAt = :now WHERE id IN (:ids)"
                                                + " AND recipientUserId = :uid AND deletedAt IS NULL")
                                .setParameter("now", LocalDateTime.now())
                                .setParameter("ids", ids)
                                .setParameter("uid", recipientUserId).executeUpdate());
        }

        @Override
        public int softDeleteAll(Long recipientUserId) {
                return TxSupport.write(sf, s -> s.createMutationQuery(
                                "UPDATE NotificationRecipientHibernate SET deletedAt = :now"
                                                + " WHERE recipientUserId = :uid AND deletedAt IS NULL")
                                .setParameter("now", LocalDateTime.now())
                                .setParameter("uid", recipientUserId).executeUpdate());
        }
}
