package vn.com.be_crm.domain.order.enums;

import vn.com.be_crm.core.error.frontend.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái đơn hàng theo vòng đời chốt bán:
 * draft → confirmed → processing → completed; có thể cancelled ở các bước trước khi hoàn tất.
 * completed đạt được khi đơn hàng đã xuất hóa đơn.
 * Mở lại (phòng lỡ bấm nhầm, chỉ khi đơn chưa `isLocked`): completed → processing, cancelled → draft.
 */
public enum OrderStatus {
    draft, confirmed, processing, completed, cancelled;

    /** Bảng các bước chuyển hợp lệ giữa các trạng thái. */
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            draft, Set.of(confirmed, cancelled),
            confirmed, Set.of(processing, completed, cancelled),
            processing, Set.of(completed, cancelled),
            completed, Set.of(processing),
            cancelled, Set.of(draft)
    );

    /**
     * Đảm bảo bước chuyển từ trạng thái hiện tại sang target là hợp lệ, nếu không ném DomainException.
     * @param target trạng thái đích
     */
    public void ensureCanTransitionTo(OrderStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển đơn hàng từ '" + this + "' sang '" + target + "'");
        }
    }
}
