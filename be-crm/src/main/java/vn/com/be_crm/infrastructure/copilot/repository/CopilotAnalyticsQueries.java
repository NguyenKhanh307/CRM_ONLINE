package vn.com.be_crm.infrastructure.copilot.repository;

import org.hibernate.Session;
import vn.com.be_crm.application.copilot.dto.CopilotChartSegment;
import vn.com.be_crm.domain.dashboard.model.DateRange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static vn.com.be_crm.infrastructure.copilot.repository.CopilotSqlSupport.*;

// SQL tổng hợp cho ngữ cảnh Copilot: chuỗi doanh thu 24 tháng, các bảng xếp hạng (nhân viên/chiến dịch/
// sản phẩm/khách hàng) và số lượng bản ghi theo từng phân hệ.
// Mọi query đều loại bản ghi đã xóa mềm/trong Thùng rác bằng deleted_at IS NULL
// (áp cho cả bảng chính lẫn bảng được JOIN vào).
// invoices/invoice_items không còn cột total/amount lưu sẵn — mọi tổng tiền tính từ INVOICE_LINE_AMOUNT
// (xem CopilotSqlSupport), join thẳng invoice_items thay vì đọc cột đã bỏ.
final class CopilotAnalyticsQueries {

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final int SERIES_MONTHS = 24;
    private static final int TOP_N = 8;

    // số lượng công việc (hoạt động) theo nhân viên trong khoảng — activities không có deleted_at
    private static final String WORKLOAD_SQL =
            "SELECT u.full_name, COUNT(*) v FROM activities a " +
            "JOIN users u ON u.id = a.assigned_user_id AND u.deleted_at IS NULL " +
            "WHERE a.created_at >= :f AND a.created_at < :t GROUP BY u.id, u.full_name ORDER BY v DESC LIMIT " + TOP_N;

    // tỉ lệ chốt đơn theo nhân viên = số báo giá accepted / tổng báo giá tạo trong khoảng
    private static final String WINRATE_SQL =
            "SELECT u.full_name, ROUND(SUM(CASE WHEN q.status = 'accepted' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1) v " +
            "FROM quotations q JOIN users u ON u.id = q.owner_id AND u.deleted_at IS NULL " +
            "WHERE q.deleted_at IS NULL AND q.created_at >= :f AND q.created_at < :t " +
            "GROUP BY u.id, u.full_name ORDER BY v DESC LIMIT " + TOP_N;

    private CopilotAnalyticsQueries() {
    }

    // chuỗi doanh thu + số hóa đơn theo từng tháng trong 24 tháng gần nhất (tháng rỗng = 0)
    static void appendMonthlySeries(Session s, StringBuilder ctx, Long ownerId) {
        LocalDate from = LocalDate.now().withDayOfMonth(1).minusMonths(SERIES_MONTHS - 1L);
        Map<String, Object> params = new HashMap<>();
        params.put("f", from);
        if (ownerId != null) params.put("o", ownerId);

        List<Object[]> rs = rows(s, "SELECT DATE_FORMAT(i.invoice_date, '%Y-%m') p, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v, " +
                "COUNT(DISTINCT i.id) c FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f" +
                ownerClause("i.owner_id", ownerId) + " GROUP BY p ORDER BY p", params);
        Map<String, BigDecimal> rev = new HashMap<>();
        Map<String, Long> cnt = new HashMap<>();
        for (Object[] r : rs) {
            rev.put(str(r[0]), toBig(r[1]));
            cnt.put(str(r[0]), ((Number) r[2]).longValue());
        }

        ctx.append("=== DOANH THU THEO THÁNG (").append(SERIES_MONTHS)
                .append(" tháng gần nhất, theo ngày hóa đơn) ===\n");
        LocalDate m = from;
        for (int i = 0; i < SERIES_MONTHS; i++) {
            String key = m.format(YM);
            ctx.append(String.format("Tháng %02d/%d: ", m.getMonthValue(), m.getYear()))
                    .append(money(rev.getOrDefault(key, BigDecimal.ZERO))).append(" đ (")
                    .append(cnt.getOrDefault(key, 0L)).append(" hóa đơn)\n");
            m = m.plusMonths(1);
        }
        ctx.append("\n");
    }

    // bốn bảng xếp hạng doanh thu trong khoảng chỉ định (top 8 mỗi loại) — khối "theo nhân viên"
    // chỉ dựng cho ADMIN/quản lý. "Theo chiến dịch" suy ra qua chuỗi Invoice -> Order -> Quotation ->
    // Opportunity -> campaign_id (invoices không còn campaign_id trực tiếp); "theo khách hàng" suy ra
    // qua Invoice -> Order -> Quotation -> customer_id (invoices không còn customer_id trực tiếp)
    static void appendRankings(Session s, StringBuilder ctx, LocalDate from, LocalDate to,
                               String rangeLabel, Long ownerId, boolean isPrivileged) {
        Map<String, Object> p = dateOwner(from, to, ownerId);
        String ofInv = ownerClause("i.owner_id", ownerId);
        ctx.append("=== XẾP HẠNG (").append(rangeLabel).append(", top ").append(TOP_N).append(") ===\n");

        if (isPrivileged) {
            appendRanked(s, ctx, "Doanh thu theo nhân viên",
                    "SELECT u.full_name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                            "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                            "JOIN users u ON u.id = i.owner_id AND u.deleted_at IS NULL " +
                            "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                            "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                            " GROUP BY u.id, u.full_name ORDER BY v DESC LIMIT " + TOP_N, p, " đ");
            appendRanked(s, ctx, "Số lượng công việc theo nhân viên", WORKLOAD_SQL, p, " việc");
            appendRanked(s, ctx, "Tỉ lệ chốt đơn theo nhân viên (báo giá)", WINRATE_SQL, p, "%");
        }
        appendRanked(s, ctx, "Doanh thu theo chiến dịch",
                "SELECT camp.name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                        "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                        "JOIN orders o ON o.id = i.order_id " +
                        "JOIN quotations q ON q.id = o.quotation_id " +
                        "JOIN opportunities opp ON opp.id = q.opportunity_id " +
                        "JOIN campaigns camp ON camp.id = opp.campaign_id AND camp.deleted_at IS NULL " +
                        "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                        "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                        " GROUP BY camp.id, camp.name ORDER BY v DESC LIMIT " + TOP_N, p, " đ");
        appendRanked(s, ctx, "Doanh thu theo khách hàng",
                "SELECT cust.name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                        "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                        "JOIN orders o ON o.id = i.order_id " +
                        "JOIN quotations q ON q.id = o.quotation_id " +
                        "JOIN customers cust ON cust.id = q.customer_id AND cust.deleted_at IS NULL " +
                        "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                        "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                        " GROUP BY cust.id, cust.name ORDER BY v DESC LIMIT " + TOP_N, p, " đ");

        // sản phẩm: invoice_items KHÔNG có deleted_at → lọc soft-delete qua hóa đơn cha
        List<Object[]> prod = rows(s, "SELECT p.name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v, COALESCE(SUM(ii.quantity),0) q " +
                "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                "JOIN products p ON p.id = ii.product_id AND p.deleted_at IS NULL " +
                "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                " GROUP BY p.id, p.name ORDER BY v DESC LIMIT " + TOP_N, p);
        if (!prod.isEmpty()) {
            ctx.append("Doanh thu theo sản phẩm: ");
            List<String> parts = prod.stream()
                    .map(r -> str(r[0]) + " = " + money(toBig(r[1])) + " đ (SL " + money(toBig(r[2])) + ")")
                    .toList();
            ctx.append(String.join("; ", parts)).append("\n");
        }
        ctx.append("\n");
    }

    // dữ liệu có cấu trúc (thay vì nối chuỗi) cho biểu đồ tròn so sánh ở /phan-tich — tái dùng nguyên
    // văn 4 câu SQL của appendRankings (cùng điều kiện lọc owner/thời gian/quyền), chỉ đổi cách trả kết
    // quả. topic không khớp category nào -> trả rỗng (use case tự fallback "kỳ này vs kỳ trước").
    static List<CopilotChartSegment> chartRows(Session s, String topic, LocalDate from, LocalDate to,
                                               Long ownerId, boolean isPrivileged) {
        Map<String, Object> p = dateOwner(from, to, ownerId);
        String ofInv = ownerClause("i.owner_id", ownerId);
        String sql = switch (topic == null ? "" : topic) {
            case "employee" -> isPrivileged ?
                    "SELECT u.full_name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                            "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                            "JOIN users u ON u.id = i.owner_id AND u.deleted_at IS NULL " +
                            "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                            "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                            " GROUP BY u.id, u.full_name ORDER BY v DESC LIMIT " + TOP_N : null;
            case "workload" -> isPrivileged ? WORKLOAD_SQL : null;
            case "winrate" -> isPrivileged ? WINRATE_SQL : null;
            case "campaign" -> "SELECT camp.name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                    "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                    "JOIN orders o ON o.id = i.order_id " +
                    "JOIN quotations q ON q.id = o.quotation_id " +
                    "JOIN opportunities opp ON opp.id = q.opportunity_id " +
                    "JOIN campaigns camp ON camp.id = opp.campaign_id AND camp.deleted_at IS NULL " +
                    "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                    "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                    " GROUP BY camp.id, camp.name ORDER BY v DESC LIMIT " + TOP_N;
            case "customer" -> "SELECT cust.name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                    "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                    "JOIN orders o ON o.id = i.order_id " +
                    "JOIN quotations q ON q.id = o.quotation_id " +
                    "JOIN customers cust ON cust.id = q.customer_id AND cust.deleted_at IS NULL " +
                    "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                    "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                    " GROUP BY cust.id, cust.name ORDER BY v DESC LIMIT " + TOP_N;
            case "product" -> "SELECT p.name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                    "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                    "JOIN products p ON p.id = ii.product_id AND p.deleted_at IS NULL " +
                    "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                    "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                    " GROUP BY p.id, p.name ORDER BY v DESC LIMIT " + TOP_N;
            default -> null;
        };
        if (sql == null) return List.of();
        List<CopilotChartSegment> out = new ArrayList<>();
        for (Object[] r : rows(s, sql, p)) out.add(new CopilotChartSegment(str(r[0]), toBig(r[1])));
        return out;
    }

    // số lượng bản ghi từng phân hệ: tổng cộng và phát sinh trong khoảng
    static void appendModuleCounts(Session s, StringBuilder ctx, DateRange cur, String rangeLabel, Long ownerId) {
        ctx.append("=== SỐ LƯỢNG BẢN GHI THEO PHÂN HỆ (tổng | phát sinh ").append(rangeLabel)
                .append(", theo ngày tạo) ===\n");
        appendCount(s, ctx, "Tiềm năng", "leads", "owner_id", cur, ownerId);
        appendCount(s, ctx, "Liên hệ", "contacts", "assigned_user_id", cur, ownerId);
        appendCount(s, ctx, "Khách hàng", "customers", "owner_id", cur, ownerId);
        appendCount(s, ctx, "Cơ hội", "opportunities", "owner_id", cur, ownerId);
        appendCount(s, ctx, "Báo giá", "quotations", "owner_id", cur, ownerId);
        appendCount(s, ctx, "Đơn hàng", "orders", "owner_id", cur, ownerId);
        appendCount(s, ctx, "Hóa đơn", "invoices", "owner_id", cur, ownerId);
        appendCount(s, ctx, "Phiếu chăm sóc", "support_tickets", "assigned_user_id", cur, ownerId);
        appendCount(s, ctx, "Chiến dịch", "campaigns", "owner_id", cur, ownerId);
        appendCount(s, ctx, "Sản phẩm", "products", null, cur, null);
        ctx.append("\n");
    }

    // chạy một truy vấn xếp hạng [nhãn, giá trị] rồi nối vào ngữ cảnh (bỏ qua nếu rỗng)
    private static void appendRanked(Session s, StringBuilder ctx, String title, String sql,
                                     Map<String, Object> params, String unit) {
        List<Object[]> rs = rows(s, sql, params);
        if (rs.isEmpty()) return;
        ctx.append(title).append(": ");
        List<String> parts = rs.stream().map(r -> str(r[0]) + " = " + money(toBig(r[1])) + unit).toList();
        ctx.append(String.join("; ", parts)).append("\n");
    }

    // đếm tổng + phát sinh trong kỳ cho một bảng nghiệp vụ (luôn loại bản ghi đã xóa)
    private static void appendCount(Session s, StringBuilder ctx, String label, String table,
                                    String ownerCol, DateRange cur, Long ownerId) {
        String of = ownerCol == null ? "" : ownerClause(ownerCol, ownerId);
        long total = count(s, "SELECT COUNT(*) FROM " + table + " WHERE deleted_at IS NULL" + of, owner(ownerId));
        long inPeriod = count(s, "SELECT COUNT(*) FROM " + table + " WHERE deleted_at IS NULL " +
                "AND created_at >= :f AND created_at < :t" + of, dateOwner(cur, ownerId));
        ctx.append(label).append(": ").append(total).append(" | ").append(inPeriod).append("\n");
    }
}
