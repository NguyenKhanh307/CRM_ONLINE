package vn.com.be_crm.infrastructure.notification.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.notification.entity.Notification;
import vn.com.be_crm.domain.notification.repository.INotificationRepository;
import vn.com.be_crm.infrastructure.notification.entity.NotificationHibernate;
import vn.com.be_crm.infrastructure.notification.mapper.NotificationHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

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
        return TxSupport.write(sf, s -> {
            NotificationHibernate m = s.merge(mapper.toHibernate(n));
            return mapper.toDomain(m);
        });
    }

    /** Lấy danh sách thông báo của người nhận, mới nhất trước. @param recipientUserId ID @param limit số tối đa */
    @Override public List<Notification> findByRecipient(Long recipientUserId, int limit) {
        return TxSupport.read(sf, s -> {
            return s.createQuery(
                    "FROM NotificationHibernate WHERE recipientUserId = :uid AND deletedAt IS NULL ORDER BY createdAt DESC",
                    NotificationHibernate.class)
                    .setParameter("uid", recipientUserId).setMaxResults(limit).list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }

    /** Đếm số thông báo chưa đọc. @param recipientUserId ID @return số lượng */
    @Override public long countUnread(Long recipientUserId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery(
                    "SELECT COUNT(n) FROM NotificationHibernate n WHERE n.recipientUserId = :uid AND n.isRead = false"
                            + " AND n.deletedAt IS NULL",
                    Long.class).setParameter("uid", recipientUserId).uniqueResult();
        });
    }

    /** Đánh dấu một thông báo đã đọc (đúng người nhận). @param id ID @param recipientUserId ID người nhận */
    @Override public void markRead(Long id, Long recipientUserId) {
        TxSupport.writeVoid(sf, s -> {
            s.createMutationQuery(
                    "UPDATE NotificationHibernate SET isRead = true WHERE id = :id AND recipientUserId = :uid"
                            + " AND deletedAt IS NULL")
                    .setParameter("id", id).setParameter("uid", recipientUserId).executeUpdate();
            });
    }

    /** Đánh dấu tất cả thông báo của người nhận đã đọc. @param recipientUserId ID người nhận */
    @Override public void markAllRead(Long recipientUserId) {
        TxSupport.writeVoid(sf, s -> {
            s.createMutationQuery(
                    "UPDATE NotificationHibernate SET isRead = true WHERE recipientUserId = :uid AND isRead = false"
                            + " AND deletedAt IS NULL")
                    .setParameter("uid", recipientUserId).executeUpdate();
            });
    }

    /** Xóa mềm các thông báo được chọn. @param ids danh sách ID @param recipientUserId ID người nhận @return số dòng */
    @Override public int softDeleteByIds(List<Long> ids, Long recipientUserId) {
        // HQL "IN ()" với danh sách rỗng sinh SQL không hợp lệ → chặn ngay từ đây
        if (ids == null || ids.isEmpty()) return 0;
        return TxSupport.write(sf, s ->
                s.createMutationQuery(
                        "UPDATE NotificationHibernate SET deletedAt = :now WHERE id IN (:ids)"
                                + " AND recipientUserId = :uid AND deletedAt IS NULL")
                        .setParameter("now", LocalDateTime.now())
                        .setParameter("ids", ids)
                        .setParameter("uid", recipientUserId).executeUpdate());
    }

    /** Xóa mềm toàn bộ thông báo còn lại của người nhận. @param recipientUserId ID người nhận @return số dòng */
    @Override public int softDeleteAll(Long recipientUserId) {
        return TxSupport.write(sf, s ->
                s.createMutationQuery(
                        "UPDATE NotificationHibernate SET deletedAt = :now"
                                + " WHERE recipientUserId = :uid AND deletedAt IS NULL")
                        .setParameter("now", LocalDateTime.now())
                        .setParameter("uid", recipientUserId).executeUpdate());
    }
}
