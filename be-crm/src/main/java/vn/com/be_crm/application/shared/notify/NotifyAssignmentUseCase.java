package vn.com.be_crm.application.shared.notify;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.shared.security.ICurrentUser;

import java.util.List;
import java.util.Objects;

/**
 * Use case dùng chung: báo cho nhân viên biết mình vừa được giao việc.
 *
 * <p>Bản ghi chưa có người phụ trách thì chỉ quản lý nhận thông báo (xem {@link IManagerResolver});
 * khi quản lý gán/bàn giao người phụ trách, chính người nhận việc được báo tại đây — nhờ vậy
 * không phải phát tán thông báo cho toàn bộ nhân viên.
 *
 * <p>Loại thông báo dùng khuôn <code>&lt;moduleKey&gt;_assigned</code> để frontend suy ra route và
 * chấm màu trên sidebar theo tiền tố trước dấu gạch dưới.
 */
public class NotifyAssignmentUseCase {

    /** Loại thông báo cho việc bàn giao toàn bộ công việc (không gắn với một bản ghi cụ thể). */
    private static final String TYPE_HANDOVER_ALL = "handover_all";

    private final CreateNotificationUseCase createNotificationUC;
    private final ICurrentUser currentUser;

    /**
     * @param createNotificationUC use case tạo thông báo
     * @param currentUser          port đọc người dùng đang thao tác
     */
    public NotifyAssignmentUseCase(CreateNotificationUseCase createNotificationUC, ICurrentUser currentUser) {
        this.createNotificationUC = createNotificationUC;
        this.currentUser = currentUser;
    }

    /**
     * Báo cho người vừa được giao MỘT bản ghi.
     *
     * @param moduleKey  khóa module (lead/customer/opportunity/quotation/order)
     * @param noun       danh từ tiếng Việt của phân hệ, vd "tiềm năng"
     * @param newOwnerId ID người phụ trách mới
     * @param recordId   ID bản ghi (dùng cho điều hướng + focus ở frontend)
     * @param code       mã bản ghi hiển thị trong nội dung
     */
    public void notifyAssigned(String moduleKey, String noun, Long newOwnerId, Long recordId, String code) {
        if (skip(newOwnerId)) return;
        String label = code != null && !code.isBlank() ? " " + code : "";
        createNotificationUC.execute(List.of(newOwnerId), moduleKey + "_assigned",
                "Bạn được giao " + noun + label,
                "Bạn vừa được giao phụ trách " + noun + label + ". Mở bản ghi để xem chi tiết.",
                null, recordId);
    }

    /**
     * Báo cho người vừa nhận bàn giao NHIỀU bản ghi cùng lúc (gộp một thông báo, không spam từng bản ghi).
     *
     * @param moduleKey  khóa module
     * @param noun       danh từ tiếng Việt của phân hệ
     * @param newOwnerId ID người nhận bàn giao
     * @param count      số bản ghi được bàn giao
     */
    public void notifyHandover(String moduleKey, String noun, Long newOwnerId, int count) {
        if (skip(newOwnerId) || count <= 0) return;
        createNotificationUC.execute(List.of(newOwnerId), moduleKey + "_assigned",
                "Bạn được bàn giao " + count + " " + noun,
                "Bạn vừa được bàn giao " + count + " " + noun + ". Mở danh sách để xem chi tiết.",
                null, null);
    }

    /**
     * Báo cho người vừa nhận bàn giao toàn bộ công việc của một người khác.
     *
     * @param newOwnerId ID người nhận bàn giao
     */
    public void notifyHandoverAll(Long newOwnerId) {
        if (skip(newOwnerId)) return;
        createNotificationUC.execute(List.of(newOwnerId), TYPE_HANDOVER_ALL,
                "Bạn được bàn giao toàn bộ công việc",
                "Toàn bộ tiềm năng, khách hàng, cơ hội, báo giá và hóa đơn của một người dùng khác"
                        + " đã được chuyển sang cho bạn.",
                null, null);
    }

    /** Bỏ qua khi không có người nhận, hoặc người thao tác tự giao việc cho chính mình. */
    private boolean skip(Long newOwnerId) {
        return newOwnerId == null || Objects.equals(newOwnerId, currentUser.id());
    }
}
