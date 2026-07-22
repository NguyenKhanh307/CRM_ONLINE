package vn.com.be_crm.infrastructure.copilot.repository;

import org.hibernate.Session;
import vn.com.be_crm.domain.dashboard.model.DateRange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static vn.com.be_crm.infrastructure.copilot.repository.CopilotSqlSupport.*;

/**
 * SQL tổng hợp cho ngữ cảnh Copilot: chuỗi doanh thu 24 tháng, các bảng xếp hạng (nhân viên/chiến dịch/
 * sản phẩm/khách hàng) và số lượng bản ghi theo từng phân hệ.
 * <p>Mọi query đều loại bản ghi đã xóa mềm/trong Thùng rác bằng {@code deleted_at IS NULL}
 * (áp cho cả bảng chính lẫn bảng được JOIN vào).
 */
final class CopilotAnalyticsQueries {

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final int SERIES_MONTHS = 24;
    private static final int TOP_N = 8;

    private CopilotAnalyticsQueries() {
    }

    /**
     * Chuỗi doanh thu + số hóa đơn theo từng tháng trong 24 tháng gần nhất (tháng rỗng = 0).
     *
     * @param s       session Hibernate
     * @param ctx     bộ đệm ngữ cảnh
     * @param ownerId lọc người phụ trách (null = toàn bộ)
     */
    static void appendMonthlySeries(Session s, StringBuilder ctx, Long ownerId) {
        LocalDate from = LocalDate.now().withDayOfMonth(1).minusMonths(SERIES_MONTHS - 1L);
        Map<String, Object> params = new HashMap<>();
        params.put("f", from);
        if (ownerId != null) params.put("o", ownerId);

        List<Object[]> rs = rows(s, "SELECT DATE_FORMAT(invoice_date, '%Y-%m') p, COALESCE(SUM(total),0) v, COUNT(*) c " +
                "FROM invoices WHERE status <> 'cancelled' AND deleted_at IS NULL AND invoice_date >= :f" +
                ownerClause("owner_id", ownerId) + " GROUP BY p ORDER BY p", params);
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

    /**
     * Bốn bảng xếp hạng doanh thu trong khoảng chỉ định (top 8 mỗi loại).
     * Khối "theo nhân viên" chỉ dựng cho ADMIN/quản lý.
     *
     * @param s            session Hibernate
     * @param ctx          bộ đệm ngữ cảnh
     * @param from         mốc bắt đầu
     * @param to           mốc kết thúc (không bao gồm)
     * @param rangeLabel   nhãn khoảng để hiển thị
     * @param ownerId      lọc người phụ trách (null = toàn bộ)
     * @param isPrivileged true nếu ADMIN/SALES_MANAGER
     */
    static void appendRankings(Session s, StringBuilder ctx, LocalDate from, LocalDate to,
                               String rangeLabel, Long ownerId, boolean isPrivileged) {
        Map<String, Object> p = dateOwner(from, to, ownerId);
        String ofInv = ownerClause("i.owner_id", ownerId);
        ctx.append("=== XẾP HẠNG (").append(rangeLabel).append(", top ").append(TOP_N).append(") ===\n");

        if (isPrivileged) {
            appendRanked(s, ctx, "Doanh thu theo nhân viên",
                    "SELECT u.full_name, COALESCE(SUM(i.total),0) v FROM invoices i " +
                            "JOIN users u ON u.id = i.owner_id AND u.deleted_at IS NULL " +
                            "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                            "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                            " GROUP BY u.id, u.full_name ORDER BY v DESC LIMIT " + TOP_N, p, " đ");
        }
        appendRanked(s, ctx, "Doanh thu theo chiến dịch",
                "SELECT c.name, COALESCE(SUM(i.total),0) v FROM invoices i " +
                        "JOIN campaigns c ON c.id = i.campaign_id AND c.deleted_at IS NULL " +
                        "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                        "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                        " GROUP BY c.id, c.name ORDER BY v DESC LIMIT " + TOP_N, p, " đ");
        appendRanked(s, ctx, "Doanh thu theo khách hàng",
                "SELECT c.name, COALESCE(SUM(i.total),0) v FROM invoices i " +
                        "JOIN customers c ON c.id = i.customer_id AND c.deleted_at IS NULL " +
                        "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL " +
                        "AND i.invoice_date >= :f AND i.invoice_date < :t" + ofInv +
                        " GROUP BY c.id, c.name ORDER BY v DESC LIMIT " + TOP_N, p, " đ");

        // Sản phẩm: invoice_items KHÔNG có deleted_at → lọc soft-delete qua hóa đơn cha.
        List<Object[]> prod = rows(s, "SELECT p.name, COALESCE(SUM(ii.amount),0) v, COALESCE(SUM(ii.quantity),0) q " +
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

    /**
     * Số lượng bản ghi từng phân hệ: tổng cộng và phát sinh trong khoảng.
     *
     * @param s          session Hibernate
     * @param ctx        bộ đệm ngữ cảnh
     * @param cur        khoảng kỳ hiện tại
     * @param rangeLabel nhãn khoảng
     * @param ownerId    lọc người phụ trách (null = toàn bộ)
     */
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

    /** Chạy một truy vấn xếp hạng [nhãn, giá trị] rồi nối vào ngữ cảnh (bỏ qua nếu rỗng). */
    private static void appendRanked(Session s, StringBuilder ctx, String title, String sql,
                                     Map<String, Object> params, String unit) {
        List<Object[]> rs = rows(s, sql, params);
        if (rs.isEmpty()) return;
        ctx.append(title).append(": ");
        List<String> parts = rs.stream().map(r -> str(r[0]) + " = " + money(toBig(r[1])) + unit).toList();
        ctx.append(String.join("; ", parts)).append("\n");
    }

    /** Đếm tổng + phát sinh trong kỳ cho một bảng nghiệp vụ (luôn loại bản ghi đã xóa). */
    private static void appendCount(Session s, StringBuilder ctx, String label, String table,
                                    String ownerCol, DateRange cur, Long ownerId) {
        String of = ownerCol == null ? "" : ownerClause(ownerCol, ownerId);
        long total = count(s, "SELECT COUNT(*) FROM " + table + " WHERE deleted_at IS NULL" + of, owner(ownerId));
        long inPeriod = count(s, "SELECT COUNT(*) FROM " + table + " WHERE deleted_at IS NULL " +
                "AND created_at >= :f AND created_at < :t" + of, dateOwner(cur, ownerId));
        ctx.append(label).append(": ").append(total).append(" | ").append(inPeriod).append("\n");
    }
}
