package vn.com.be_crm.application.notification.query;

import vn.com.be_crm.domain.notification.repository.INotificationRepository;

/** Use case đếm số thông báo chưa đọc của người dùng hiện tại. */
public class CountUnreadNotificationsUseCase {
    private final INotificationRepository repository;

    /** @param repository port lưu trữ Notification */
    public CountUnreadNotificationsUseCase(INotificationRepository repository) { this.repository = repository; }

    /**
     * Đếm số thông báo chưa đọc.
     * @param userId ID người dùng @return số lượng chưa đọc
     */
    public long execute(Long userId) {
        return repository.countUnread(userId);
    }
}
