package vn.com.be_crm.domain.copilot.repository;

import vn.com.be_crm.application.copilot.dto.CopilotQuerySpec;
import vn.com.be_crm.application.copilot.dto.NlQueryResult;

/**
 * Port thực thi truy vấn có cấu trúc (NL2SQL có kiểm soát) — LLM chỉ chọn tham số trong
 * {@link CopilotQuerySpec}, port này tự dựng SQL an toàn từ whitelist và tính số THẬT.
 */
public interface INlQueryEngine {

    /**
     * Chạy một truy vấn có cấu trúc trong đúng phạm vi quyền của người hỏi.
     *
     * @param queryType    "AGGREGATE" (đếm/tổng/tỉ lệ, có thể nhóm theo) hoặc "LIST" (liệt kê bản
     *                     ghi khớp điều kiện đã đăng ký sẵn)
     * @param spec         tham số do LLM chọn (module/metric/groupBy/condition/employeeNames/status)
     * @param question     câu hỏi gốc (dùng để suy khoảng thời gian, tái dùng CopilotRangeParser)
     * @param ownerId      phạm vi của người hỏi (null nếu không giới hạn)
     * @param isPrivileged true nếu ADMIN/SALES_MANAGER (được xem toàn đội, được nêu tên nhân viên khác)
     * @return kết quả đã tính (valid=false nếu spec không khớp whitelist)
     */
    NlQueryResult run(String queryType, CopilotQuerySpec spec, String question, Long ownerId, boolean isPrivileged);
}
