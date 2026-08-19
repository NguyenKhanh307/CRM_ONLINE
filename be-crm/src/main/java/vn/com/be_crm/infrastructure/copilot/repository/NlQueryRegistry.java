package vn.com.be_crm.infrastructure.copilot.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

// Whitelist DUY NHẤT cho engine NL2SQL có kiểm soát (NlQueryEngineImpl) — LLM chỉ được chọn một
// khóa (module/metric/groupBy/condition) trong các danh sách dưới đây; Java tự ánh xạ khóa sang
// tên bảng/cột THẬT (hằng số Java, không bao giờ lấy chuỗi từ LLM ghép thẳng vào SQL).
// Tách khỏi NlQueryEngineImpl để giữ file dưới 400 dòng theo quy ước dự án.
final class NlQueryRegistry {

    // cấu hình một module: bảng, cột "người phụ trách" (owner/assigned), cột trạng thái (null nếu
    // không có), có cột deleted_at hay không (activities không có soft-delete)
    record ModuleConfig(String table, String ownerColumn, String statusColumn, boolean softDelete) {
    }

    static final Map<String, ModuleConfig> MODULES = Map.ofEntries(
            Map.entry("lead", new ModuleConfig("leads", "owner_id", "status", true)),
            Map.entry("contact", new ModuleConfig("contacts", "assigned_user_id", null, true)),
            Map.entry("customer", new ModuleConfig("customers", "owner_id", "status", true)),
            Map.entry("opportunity", new ModuleConfig("opportunities", "owner_id", "status", true)),
            Map.entry("quotation", new ModuleConfig("quotations", "owner_id", "status", true)),
            Map.entry("order", new ModuleConfig("orders", "owner_id", "status", true)),
            Map.entry("invoice", new ModuleConfig("invoices", "owner_id", "status", true)),
            Map.entry("activity", new ModuleConfig("activities", "assigned_user_id", "status", false)),
            Map.entry("ticket", new ModuleConfig("support_tickets", "assigned_user_id", "status", true)),
            Map.entry("campaign", new ModuleConfig("campaigns", "owner_id", "status", true)),
            Map.entry("product", new ModuleConfig("products", null, null, true)));

    // nhãn tiếng Việt hiển thị trong câu trả lời tự dựng
    static final Map<String, String> MODULE_LABELS = Map.ofEntries(
            Map.entry("lead", "tiềm năng"), Map.entry("contact", "liên hệ"),
            Map.entry("customer", "khách hàng"), Map.entry("opportunity", "cơ hội"),
            Map.entry("quotation", "báo giá"), Map.entry("order", "đơn hàng"),
            Map.entry("invoice", "hóa đơn"), Map.entry("activity", "hoạt động"),
            Map.entry("ticket", "phiếu chăm sóc"), Map.entry("campaign", "chiến dịch"),
            Map.entry("product", "sản phẩm"));

    // module cho phép metric=SUM_AMOUNT — công thức tiền của mỗi module khác nhau (xem
    // NlQueryEngineImpl#amountExpr), KHÔNG generic hóa để tránh sai công thức
    static final Set<String> SUM_AMOUNT_MODULES = Set.of("opportunity", "invoice");

    // module cho phép metric=RATE_ACCEPTED (chỉ báo giá: accepted / tổng)
    static final Set<String> RATE_MODULES = Set.of("quotation");

    // điều kiện LIST hợp lệ theo module — mỗi điều kiện là một quy tắc nghiệp vụ ĐÃ CÀI SẴN ở nơi
    // khác trong hệ thống, tái dùng nguyên công thức (xem NlQueryEngineImpl#listConditionSql)
    static final Map<String, Set<String>> LIST_CONDITIONS = Map.of(
            "quotation", Set.of("EXPIRED"),
            "ticket", Set.of("OVERDUE"),
            "invoice", Set.of("PAYMENT_OVERDUE"),
            "opportunity", Set.of("STALLED"));

    static final List<String> METRICS = List.of("COUNT", "SUM_AMOUNT", "RATE_ACCEPTED");
    static final List<String> GROUP_BYS = List.of("NONE", "OWNER", "MONTH", "STATUS");
    static final List<String> QUERY_TYPES = List.of("AGGREGATE", "LIST");

    private NlQueryRegistry() {
    }
}
