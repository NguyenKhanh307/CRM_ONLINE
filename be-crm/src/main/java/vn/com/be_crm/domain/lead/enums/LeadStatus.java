package vn.com.be_crm.domain.lead.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import vn.com.be_crm.core.error.frontend.DomainException;

import java.util.Map;
import java.util.Set;

// trạng thái tiềm năng — đổi qua hành động/tự động (chấm điểm), không sửa tay
// new → contacting → converted; converted chỉ cho phép khi tiềm năng đã có đơn hàng đầu tiên
// (guard ở UpdateLeadUseCase, không phải ở đây) — đã bỏ qualified/lost theo yêu cầu đơn giản hóa
public enum LeadStatus {
    new_, contacting, converted;

    // new_ là từ khóa Java nên phải đổi tên; DB/JSON vẫn dùng "new"
    @JsonValue
    public String toJson() {
        return this == new_ ? "new" : name();
    }

    @JsonCreator
    public static LeadStatus fromDb(String v) {
        if ("new".equals(v)) return new_;
        return valueOf(v);
    }

    private static final Map<LeadStatus, Set<LeadStatus>> ALLOWED = Map.of(
            new_, Set.of(contacting, converted),
            contacting, Set.of(converted),
            converted, Set.of()
    );

    // không ném exception — dùng cho luồng tự động (chấm điểm), bước chuyển sai chỉ nghĩa
    // là giữ nguyên trạng thái, không phải lỗi người dùng
    public boolean canTransitionTo(LeadStatus target) {
        return ALLOWED.getOrDefault(this, Set.of()).contains(target);
    }

    // dùng cho hành động người dùng bấm tay (đổi status qua Update) — sai bước thì báo lỗi
    public void ensureCanTransitionTo(LeadStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển tiềm năng từ '" + toJson() + "' sang '" + target.toJson() + "'");
        }
    }
}
