package vn.com.be_crm.application.notification.query;

import vn.com.be_crm.application.notification.dto.NotificationResult;
import vn.com.be_crm.application.notification.mapper.NotificationMapper;
import vn.com.be_crm.domain.notification.repository.INotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách thông báo của người dùng hiện tại. */
public class ListMyNotificationsUseCase {
    private static final int LIMIT = 50;
    private final INotificationRepository repository;

    /** @param repository port lưu trữ Notification */
    public ListMyNotificationsUseCase(INotificationRepository repository) { this.repository = repository; }

    /**
     * Lấy tối đa 50 thông báo mới nhất của người dùng.
     * @param userId ID người dùng @return danh sách NotificationResult
     */
    public List<NotificationResult> execute(Long userId) {
        return repository.findByRecipient(userId, LIMIT).stream()
                .map(NotificationMapper::toResult).collect(Collectors.toList());
    }
}
