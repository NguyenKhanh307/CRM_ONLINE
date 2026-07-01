package vn.com.be_crm.domain.service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Loại yêu cầu sau bán. 'return' là từ khóa Java nên dùng hằng return_ với
 * ánh xạ JSON/DB về "return" (giống cách xử lý LeadStatus.new_).
 */
public enum TicketType {
    support, return_, exchange, complaint;

    /** Trả về giá trị DB/JSON thực (return_ → "return"). */
    @JsonValue
    public String toJson() {
        return this == return_ ? "return" : name();
    }

    /** Ánh xạ DB/JSON value → enum (hỗ trợ "return" keyword). */
    @JsonCreator
    public static TicketType fromDb(String v) {
        if ("return".equals(v)) return return_;
        return valueOf(v);
    }
}
