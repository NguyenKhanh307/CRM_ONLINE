package vn.com.be_crm.application.notification.command;

import vn.com.be_crm.domain.notification.entity.Notification;
import vn.com.be_crm.domain.notification.repository.INotificationRepository;

import java.util.List;

// tạo MỘT thông báo dùng chung cho nhiều người nhận (khác trước đây: mỗi người nhận từng có
// một dòng notifications riêng trùng lặp nội dung — nay chỉ 1 dòng notifications + N dòng
// notification_recipients)
public class CreateNotificationUseCase {
    private final INotificationRepository repository;

    public CreateNotificationUseCase(INotificationRepository repository) { this.repository = repository; }

    // recipientUserIds: danh sách ID người nhận; targetType/targetId: bản ghi đích để FE điều
    // hướng + focus (cả hai có thể null cho thông báo không gắn 1 bản ghi cụ thể, vd bàn giao
    // toàn bộ công việc)
    public void execute(List<Long> recipientUserIds, String type, String title, String content, String targetType, Long targetId) {
        if (recipientUserIds == null) return;
        List<Long> uids = recipientUserIds.stream().distinct().filter(id -> id != null).toList();
        if (uids.isEmpty()) return;
        Notification n = Notification.builder()
                .type(type).title(title).content(content)
                .targetType(targetType).targetId(targetId).build();
        repository.save(n, uids);
    }
}
