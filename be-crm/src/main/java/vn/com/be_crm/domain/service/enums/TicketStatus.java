package vn.com.be_crm.domain.service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái phiếu hỗ trợ. Thay đổi qua hành động có kiểm soát (không sửa tay).
 * 'new' là từ khóa Java nên dùng hằng new_ với ánh xạ JSON/DB về "new".
 *
 * <p>Luồng support/complaint: new → assigned → in_progress → resolved → closed.
 * Luồng return/exchange: new → assigned → in_progress → approved → received → inspected → resolved → closed
 * (hoặc in_progress → rejected → closed). Mở lại: resolved/closed → reopened → (resolve/approve/reject).</p>
 */
public enum TicketStatus {
    new_, assigned, in_progress, approved, rejected, received, inspected, resolved, closed, reopened;

    /** Trả về giá trị DB/JSON thực (new_ → "new"). */
    @JsonValue
    public String toJson() {
        return this == new_ ? "new" : name();
    }

    /** Ánh xạ DB/JSON value → enum (hỗ trợ "new" keyword). */
    @JsonCreator
    public static TicketStatus fromDb(String v) {
        if ("new".equals(v)) return new_;
        return valueOf(v);
    }

    /** Bảng các bước chuyển hợp lệ giữa các trạng thái. */
    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED = Map.of(
            new_, Set.of(assigned),
            assigned, Set.of(in_progress),
            in_progress, Set.of(resolved, approved, rejected),
            approved, Set.of(received),
            received, Set.of(inspected),
            inspected, Set.of(resolved),
            rejected, Set.of(closed),
            resolved, Set.of(closed, reopened),
            closed, Set.of(reopened),
            reopened, Set.of(resolved, approved, rejected)
    );

    /**
     * Đảm bảo bước chuyển từ trạng thái hiện tại sang target là hợp lệ, nếu không ném DomainException.
     * @param target trạng thái đích
     */
    public void ensureCanTransitionTo(TicketStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển phiếu từ '" + toJson() + "' sang '" + target.toJson() + "'");
        }
    }
}
