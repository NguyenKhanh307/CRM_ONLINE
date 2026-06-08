package vn.com.be_crm.domain.lead.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Trạng thái tiềm năng. */
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
}
