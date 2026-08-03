package vn.com.be_crm.domain.invoice.enums;

import vn.com.be_crm.core.error.frontend.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái hóa đơn theo vòng đời công nợ:
 * draft → sent → partially_paid → paid; có thể cancelled ở các bước đầu.
 * partially_paid/paid được suy ra tự động từ các đợt thanh toán.
 */
public enum InvoiceStatus {
    draft, sent, partially_paid, paid, cancelled;

    /** Bảng các bước chuyển hợp lệ giữa các trạng thái. */
    private static final Map<InvoiceStatus, Set<InvoiceStatus>> ALLOWED = Map.of(
            draft, Set.of(sent, cancelled),
            sent, Set.of(partially_paid, paid, cancelled),
            partially_paid, Set.of(paid, cancelled),
            paid, Set.of(),
            cancelled, Set.of()
    );

    /**
     * Đảm bảo bước chuyển từ trạng thái hiện tại sang target là hợp lệ, nếu không ném DomainException.
     * @param target trạng thái đích
     */
    public void ensureCanTransitionTo(InvoiceStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển hóa đơn từ '" + this + "' sang '" + target + "'");
        }
    }
}
