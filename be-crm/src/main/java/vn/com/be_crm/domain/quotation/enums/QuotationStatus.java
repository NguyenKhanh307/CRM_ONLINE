package vn.com.be_crm.domain.quotation.enums;

import vn.com.be_crm.core.error.frontend.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái báo giá. Thay đổi qua hành động có kiểm soát (không sửa tay):
 * draft → pending → approved → sent, hoặc từ chối thì pending → draft (quay lại nháp để sửa).
 * Expired suy ra theo ngày hiệu lực. Mở lại (phòng lỡ bấm nhầm, chỉ khi báo giá chưa khóa): accepted → sent.
 * `rejected` chỉ dùng cho lịch sử duyệt ({@code QuotationApprovalStatus}), không phải trạng thái header.
 */
public enum QuotationStatus {
    draft, pending, approved, rejected, sent, accepted, expired;

    /** Bảng các bước chuyển hợp lệ giữa các trạng thái. */
    private static final Map<QuotationStatus, Set<QuotationStatus>> ALLOWED = Map.of(
            draft, Set.of(pending),
            pending, Set.of(approved, draft),
            approved, Set.of(sent),
            sent, Set.of(accepted),
            accepted, Set.of(sent)
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
