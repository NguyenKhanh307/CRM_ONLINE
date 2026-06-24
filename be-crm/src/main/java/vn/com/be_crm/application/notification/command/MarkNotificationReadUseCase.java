package vn.com.be_crm.application.notification.command;

import vn.com.be_crm.domain.notification.repository.INotificationRepository;

/** Use case đánh dấu một thông báo (hoặc tất cả) là đã đọc. */
public class MarkNotificationReadUseCase {
    private final INotificationRepository repository;

    /** @param repository port lưu trữ Notification */
    public MarkNotificationReadUseCase(INotificationRepository repository) { this.repository = repository; }

    /**
     * Đánh dấu một thông báo đã đọc.
     * @param id ID thông báo @param userId ID người nhận
     */
    public void markOne(Long id, Long userId) {
        repository.markRead(id, userId);
    }

    /**
     * Đánh dấu tất cả thông báo của người dùng đã đọc.
     * @param userId ID người nhận
     */
    public void markAll(Long userId) {
        repository.markAllRead(userId);
    }
}
