package vn.com.be_crm.domain.quotation.enums;

import vn.com.be_crm.core.error.frontend.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái báo giá. Thay đổi qua hành động có kiểm soát (không sửa tay):
 * draft → pending → approved/rejected → sent. Expired suy ra theo ngày hiệu lực.
 */
public enum QuotationStatus {
    draft, pending, approved, rejected, sent, accepted, expired;

    /** Bảng các bước chuyển hợp lệ giữa các trạng thái. */
    private static final Map<QuotationStatus, Set<QuotationStatus>> ALLOWED = Map.of(
            draft, Set.of(pending),
            pending, Set.of(approved, rejected),
            approved, Set.of(sent),
            rejected, Set.of(draft),
            sent, Set.of(accepted),
            accepted, Set.of()
    );

    /**
     * Đảm bảo bước chuyển từ trạng thái hiện tại sang target là hợp lệ, nếu không ném DomainException.
     * @param target trạng thái đích
     */
    public void ensureCanTransitionTo(QuotationStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển báo giá từ '" + this + "' sang '" + target + "'");
        }
    }
}
