package vn.com.be_crm.domain.customer.enums;

import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.Map;
import java.util.Set;

/** Trạng thái khách hàng. Thay đổi qua hành động (không sửa tay): active ↔ inactive. */
public enum CustomerStatus {
    active, inactive, potential;

    /** Bảng các bước chuyển hợp lệ. */
    private static final Map<CustomerStatus, Set<CustomerStatus>> ALLOWED = Map.of(
            active, Set.of(inactive),
            inactive, Set.of(active),
            potential, Set.of(active, inactive)
    );

    /**
     * Đảm bảo bước chuyển sang target hợp lệ, nếu không ném DomainException.
     * @param target trạng thái đích
     */
    public void ensureCanTransitionTo(CustomerStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển khách hàng từ '" + this + "' sang '" + target + "'");
        }
    }
}
