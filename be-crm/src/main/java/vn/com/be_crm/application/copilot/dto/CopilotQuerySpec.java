package vn.com.be_crm.application.copilot.dto;

import java.util.List;

// tham số truy vấn có cấu trúc do LLM CHỌN (không bao giờ chứa SQL) — Java build SQL an toàn từ
// các giá trị này sau khi đối chiếu whitelist (xem NlQueryRegistry ở infrastructure layer).
// metric/groupBy chỉ dùng cho queryType=AGGREGATE; condition chỉ dùng cho queryType=LIST.
public record CopilotQuerySpec(
        String module,
        String metric,
        String groupBy,
        String condition,
        List<String> employeeNames,
        String status) {
}
