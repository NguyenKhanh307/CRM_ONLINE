package vn.com.be_crm.application.notification.command;

import vn.com.be_crm.domain.notification.repository.INotificationRepository;

import java.util.List;

/**
 * Use case xóa mềm thông báo của người dùng hiện tại (dọn hộp thông báo).
 *
 * <p>Mỗi thông báo là một dòng riêng của một người nhận, nên việc xóa ở đây chỉ ảnh hưởng
 * hộp thông báo của chính người đó — người nhận khác của cùng sự kiện vẫn giữ nguyên tin.
 * Không có luồng khôi phục: dữ liệu giữ lại trong DB chỉ để tra cứu.
 */
public class DeleteNotificationsUseCase {
    private final INotificationRepository repository;

    /** @param repository port lưu trữ Notification */
    public DeleteNotificationsUseCase(INotificationRepository repository) { this.repository = repository; }

    /**
     * Xóa mềm các thông báo được chọn.
     * @param ids danh sách ID thông báo @param userId ID người nhận @return số thông báo đã xóa
     */
    public int deleteMany(List<Long> ids, Long userId) {
        return repository.softDeleteByIds(ids, userId);
    }

    /**
     * Xóa mềm toàn bộ thông báo còn lại của người dùng.
     * @param userId ID người nhận @return số thông báo đã xóa
     */
    public int deleteAll(Long userId) {
        return repository.softDeleteAll(userId);
    }
}
