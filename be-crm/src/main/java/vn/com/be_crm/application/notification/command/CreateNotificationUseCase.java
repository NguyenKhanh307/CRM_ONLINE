package vn.com.be_crm.application.notification.command;

import vn.com.be_crm.domain.notification.entity.Notification;
import vn.com.be_crm.domain.notification.repository.INotificationRepository;

import java.util.List;

/** Use case tạo thông báo cho một hoặc nhiều người nhận. */
public class CreateNotificationUseCase {
    private final INotificationRepository repository;

    /** @param repository port lưu trữ Notification */
    public CreateNotificationUseCase(INotificationRepository repository) { this.repository = repository; }

    /**
     * Tạo một thông báo cho mỗi người nhận trong danh sách.
     * @param recipientUserIds danh sách ID người nhận
     * @param type loại thông báo
     * @param title tiêu đề
     * @param content nội dung
     * @param leadId ID tiềm năng liên quan (có thể null)
     * @param targetId ID bản ghi đích để FE điều hướng + focus (có thể null)
     */
    public void execute(List<Long> recipientUserIds, String type, String title, String content, Long leadId, Long targetId) {
        if (recipientUserIds == null) return;
        recipientUserIds.stream().distinct().filter(id -> id != null).forEach(uid ->
                repository.save(Notification.builder()
                        .recipientUserId(uid).type(type).title(title).content(content)
                        .leadId(leadId).targetId(targetId).isRead(false).build()));
    }
}
