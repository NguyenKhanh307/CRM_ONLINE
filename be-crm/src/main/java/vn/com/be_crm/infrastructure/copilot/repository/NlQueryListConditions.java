package vn.com.be_crm.infrastructure.copilot.repository;

import java.util.Map;

// SQL cho từng "condition" của queryType=LIST — mỗi condition là MỘT quy tắc nghiệp vụ đã cài sẵn
// ở nơi khác trong hệ thống, tái dùng NGUYÊN VĂN công thức đó thay vì viết lại (tránh hai công thức
// "quá hạn" lệch nhau theo thời gian). Đối chiếu whitelist NlQueryRegistry.LIST_CONDITIONS trước
// khi gọi tới đây — file này không tự kiểm tra module/condition có hợp lệ hay không.
final class NlQueryListConditions {

    // selectCols/from/where dùng alias "x" cho bảng chính của module; where KHÔNG có "WHERE "
    record ListQuery(String selectCols, String from, String where, Map<String, Object> params) {
    }

    private NlQueryListConditions() {
    }

    static ListQuery get(String module, String condition) {
        return switch (module + ":" + condition) {
            // khớp QuotationExpiryUseCase: chưa xử lý xong + đã quá hạn hiệu lực
            case "quotation:EXPIRED" -> new ListQuery("x.id, x.code, x.code",
                    "FROM quotations x",
                    "x.deleted_at IS NULL AND x.status IN ('draft','pending','approved','sent') " +
                            "AND x.valid_until < CURDATE()",
                    Map.of());
            // khớp TicketCommandMapper.toResult(): quá hạn SLA + phiếu chưa đóng
            case "ticket:OVERDUE" -> new ListQuery("x.id, x.code, x.subject",
                    "FROM support_tickets x",
                    "x.deleted_at IS NULL AND x.sla_due_at IS NOT NULL AND x.sla_due_at < NOW() " +
                            "AND x.status IN ('new','assigned','in_progress','approved','received','inspected','reopened')",
                    Map.of());
            // khớp CopilotContextRepositoryImpl#appendAggregates (overdueInv) — cùng công thức
            case "invoice:PAYMENT_OVERDUE" -> new ListQuery("x.id, x.code, x.code",
                    "FROM invoices x",
                    "x.deleted_at IS NULL AND x.due_date IS NOT NULL AND x.due_date < CURDATE() " +
                            "AND x.payment_status <> 'paid' AND x.status <> 'cancelled'",
                    Map.of());
            // khớp DashboardRepositoryImpl#stalledOpportunities: đang mở + N ngày không chăm sóc
            // (N lấy đúng DashboardDefaults.STALLED_DAYS_DEFAULT = 14, không đưa vào tham số LLM)
            case "opportunity:STALLED" -> new ListQuery("x.id, x.code, x.name",
                    "FROM opportunities x LEFT JOIN " +
                            "(SELECT target_id, MAX(created_at) last_touch FROM activities " +
                            "WHERE target_type = 'opportunity' GROUP BY target_id) lt ON lt.target_id = x.id",
                    "x.deleted_at IS NULL AND x.status = 'open' " +
                            "AND COALESCE(lt.last_touch, x.created_at) < DATE_SUB(NOW(), INTERVAL 14 DAY)",
                    Map.of());
            default -> null;
        };
    }

    // nhãn tiếng Việt cho câu trả lời (mẫu: "Có N báo giá <nhãn>: ...")
    static String label(String condition) {
        return switch (condition) {
            case "EXPIRED" -> "đã hết hạn";
            case "OVERDUE" -> "đã quá hạn SLA";
            case "PAYMENT_OVERDUE" -> "quá hạn thanh toán";
            case "STALLED" -> "đang treo (không chăm sóc lâu ngày)";
            default -> "";
        };
    }
}
