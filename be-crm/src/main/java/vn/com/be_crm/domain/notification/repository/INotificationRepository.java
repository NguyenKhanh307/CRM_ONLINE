package vn.com.be_crm.domain.notification.repository;

import vn.com.be_crm.domain.notification.entity.Notification;
import vn.com.be_crm.domain.notification.entity.NotificationFeedItem;

import java.util.List;

// port lưu trữ cho Notification. Một thông báo dùng chung cho nhiều người nhận — save() ghi 1
// dòng notifications + N dòng notification_recipients; các thao tác đọc/xóa còn lại đều thao
// tác trên notification_recipients (id truyền vào là id của dòng người nhận, không phải id
// thông báo gốc).
public interface INotificationRepository {

    // lưu 1 thông báo dùng chung + N dòng người nhận trong 1 lần gọi
    Notification save(Notification notification, List<Long> recipientUserIds);

    // danh sách thông báo của một người nhận, mới nhất trước
    List<NotificationFeedItem> findByRecipient(Long recipientUserId, int limit);

    long countUnread(Long recipientUserId);

    // đánh dấu một thông báo đã đọc (id = id dòng notification_recipients, chỉ khi đúng người nhận)
    void markRead(Long id, Long recipientUserId);

    void markAllRead(Long recipientUserId);

    // xóa mềm các thông báo được chọn (id = id dòng notification_recipients, chỉ khi đúng người nhận)
    int softDeleteByIds(List<Long> ids, Long recipientUserId);

    int softDeleteAll(Long recipientUserId);
}
