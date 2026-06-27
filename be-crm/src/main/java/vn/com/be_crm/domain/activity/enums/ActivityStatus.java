package vn.com.be_crm.domain.activity.enums;

import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái hoạt động. Thay đổi qua hành động (không sửa tay):
 * planned → in_progress → done; hủy ở mọi bước trước done.
 */
public enum ActivityStatus {
    planned, in_progress, done, cancelled;

    /** Bảng các bước chuyển hợp lệ. */
    private static final Map<ActivityStatus, Set<ActivityStatus>> ALLOWED = Map.of(
            planned, Set.of(in_progress, cancelled),
            in_progress, Set.of(done, cancelled),
            done, Set.of(),
            cancelled, Set.of()
    );

    /**
     * Đảm bảo bước chuyển sang target hợp lệ, nếu không ném DomainException.
     * @param target trạng thái đích
     */
    public void ensureCanTransitionTo(ActivityStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển hoạt động từ '" + this + "' sang '" + target + "'");
        }
    }
}
