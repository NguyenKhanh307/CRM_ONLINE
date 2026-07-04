package vn.com.be_crm.domain.order.enums;

import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái đơn hàng theo vòng đời chốt bán:
 * draft → confirmed → processing → completed; có thể cancelled ở các bước trước khi hoàn tất.
 * completed đạt được khi đơn hàng đã xuất hóa đơn.
 */
public enum OrderStatus {
    draft, confirmed, processing, completed, cancelled;

    /** Bảng các bước chuyển hợp lệ giữa các trạng thái. */
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            draft, Set.of(confirmed, cancelled),
            confirmed, Set.of(processing, completed, cancelled),
            processing, Set.of(completed, cancelled),
            completed, Set.of(),
            cancelled, Set.of()
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
