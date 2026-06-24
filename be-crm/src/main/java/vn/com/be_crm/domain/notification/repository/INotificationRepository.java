package vn.com.be_crm.domain.notification.repository;

import vn.com.be_crm.domain.notification.entity.Notification;

import java.util.List;

/**
 * Port lưu trữ cho Notification.
 */
public interface INotificationRepository {

    /**
     * Lưu một thông báo.
     * @param notification domain entity @return entity sau khi lưu
     */
    Notification save(Notification notification);

    /**
     * Lấy danh sách thông báo của một người nhận, mới nhất trước.
     * @param recipientUserId ID người nhận @param limit số lượng tối đa @return danh sách
     */
    List<Notification> findByRecipient(Long recipientUserId, int limit);

    /**
     * Đếm số thông báo chưa đọc của một người nhận.
     * @param recipientUserId ID người nhận @return số lượng chưa đọc
     */
    long countUnread(Long recipientUserId);

    /**
     * Đánh dấu một thông báo đã đọc (chỉ khi đúng người nhận).
     * @param id ID thông báo @param recipientUserId ID người nhận
     */
    void markRead(Long id, Long recipientUserId);

    /**
     * Đánh dấu tất cả thông báo của người nhận là đã đọc.
     * @param recipientUserId ID người nhận
     */
    void markAllRead(Long recipientUserId);
}
