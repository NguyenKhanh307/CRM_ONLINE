package vn.com.be_crm.domain.lead.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái tiềm năng. Thay đổi qua hành động/tự động (chấm điểm), không sửa tay:
 * new → contacting → qualified → converted; lost ở mọi bước trước converted.
 */
public enum LeadStatus {
    new_, contacting, qualified, converted, lost;

    /** Trả về giá trị DB/JSON thực (new_ → "new"). */
    @JsonValue
    public String toJson() {
        return this == new_ ? "new" : name();
    }

    /** Ánh xạ DB/JSON value → enum (hỗ trợ "new" keyword). */
    @JsonCreator
    public static LeadStatus fromDb(String v) {
        if ("new".equals(v)) return new_;
        return valueOf(v);
    }

    /** Bảng các bước chuyển hợp lệ (dùng cho hành động convert/lose). */
    private static final Map<LeadStatus, Set<LeadStatus>> ALLOWED = Map.of(
            new_, Set.of(contacting, qualified, lost),
            contacting, Set.of(qualified, lost),
            qualified, Set.of(converted, lost),
            converted, Set.of(),
            lost, Set.of()
    );

    /**
     * Đảm bảo bước chuyển sang target hợp lệ, nếu không ném DomainException.
     * @param target trạng thái đích
     */
    public void ensureCanTransitionTo(LeadStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển tiềm năng từ '" + toJson() + "' sang '" + target.toJson() + "'");
        }
    }
}
