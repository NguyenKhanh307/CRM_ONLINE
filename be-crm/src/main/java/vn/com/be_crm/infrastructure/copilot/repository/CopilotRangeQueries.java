package vn.com.be_crm.infrastructure.copilot.repository;

import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static vn.com.be_crm.infrastructure.copilot.repository.CopilotSqlSupport.*;

// SQL theo KHOẢNG THỜI GIAN bất kỳ: dựng khối số liệu phủ TẤT CẢ phân hệ cho một khoảng
// (số lượng + phân rã trạng thái + giá trị tiền). Nhờ khối này Copilot trả lời được câu hỏi
// ở mọi mốc thời gian, kể cả ngày lẻ và mốc nằm ngoài cửa sổ 24 tháng.
// Mọi query đều loại bản ghi đã xóa mềm/trong Thùng rác bằng deleted_at IS NULL.
// quotations/orders/invoices không còn cột total lưu sẵn — tính on-read bằng công thức
// LineItemTotals.lineAmount join thẳng bảng dòng hàng tương ứng.
final class CopilotRangeQueries {

    private CopilotRangeQueries() {
    }

    // dựng khối số liệu đầy đủ cho một khoảng thời gian
    static void appendRangeBlock(Session s, StringBuilder ctx, LocalDate from, LocalDate to,
                                 String label, Long ownerId) {
        ctx.append("=== SỐ LIỆU KHOẢNG ").append(label).append(" ===\n");
        ctx.append("(đếm theo ngày tạo bản ghi; doanh thu theo ngày hóa đơn; đơn hàng theo ngày đặt)\n");

        Map<String, Object> p = dateOwner(from, to, ownerId);

        // Tiềm năng / Liên hệ / Khách hàng
        ctx.append("Tiềm năng: ").append(countIn(s, "leads", "owner_id", p, ownerId))
                .append(statusBreakdown(s, "leads", "owner_id", p, ownerId)).append("\n");
        ctx.append("Liên hệ: ").append(countIn(s, "contacts", "assigned_user_id", p, ownerId)).append("\n");
        ctx.append("Khách hàng: ").append(countIn(s, "customers", "owner_id", p, ownerId))
                .append(statusBreakdown(s, "customers", "owner_id", p, ownerId)).append("\n");

        // Cơ hội (kèm tổng giá trị)
        BigDecimal oppAmount = sum(s, "SELECT COALESCE(SUM(amount),0) FROM opportunities WHERE deleted_at IS NULL " +
                "AND created_at >= :f AND created_at < :t" + ownerClause("owner_id", ownerId), p);
        ctx.append("Cơ hội: ").append(countIn(s, "opportunities", "owner_id", p, ownerId))
                .append(statusBreakdown(s, "opportunities", "owner_id", p, ownerId))
                .append(" | Tổng giá trị: ").append(money(oppAmount)).append(" đ\n");

        // Báo giá — tổng tính từ quotation_items (không còn cột total lưu sẵn)
        BigDecimal quoTotal = sum(s, "SELECT COALESCE(SUM((qi.quantity*qi.unit_price - qi.discount) * (1 + qi.tax_rate/100)),0) " +
                "FROM quotations q JOIN quotation_items qi ON qi.quotation_id = q.id " +
                "WHERE q.deleted_at IS NULL AND q.created_at >= :f AND q.created_at < :t" + ownerClause("q.owner_id", ownerId), p);
        ctx.append("Báo giá: ").append(countIn(s, "quotations", "owner_id", p, ownerId))
                .append(statusBreakdown(s, "quotations", "owner_id", p, ownerId))
                .append(" | Tổng: ").append(money(quoTotal)).append(" đ\n");

        // Đơn hàng — lọc theo order_date, tổng tính từ order_items (không còn cột total lưu sẵn)
        String ofOrd = ownerClause("owner_id", ownerId);
        long orders = count(s, "SELECT COUNT(*) FROM orders WHERE deleted_at IS NULL " +
                "AND order_date >= :f AND order_date < :t" + ofOrd, p);
        BigDecimal orderTotal = sum(s, "SELECT COALESCE(SUM((oi.quantity*oi.unit_price - oi.discount) * (1 + oi.tax_rate/100)),0) " +
                "FROM orders o JOIN order_items oi ON oi.order_id = o.id " +
                "WHERE o.deleted_at IS NULL AND o.status <> 'cancelled' " +
                "AND o.order_date >= :f AND o.order_date < :t" + ownerClause("o.owner_id", ownerId), p);
        ctx.append("Đơn hàng: ").append(orders)
                .append(breakdown(s, "SELECT status, COUNT(*) FROM orders WHERE deleted_at IS NULL " +
                        "AND order_date >= :f AND order_date < :t" + ofOrd + " GROUP BY status", p))
                .append(" | Tổng: ").append(money(orderTotal)).append(" đ\n");

        // Hóa đơn + doanh thu — lọc theo invoice_date, doanh thu tính từ invoice_items (không còn
        // cột total lưu sẵn)
        String ofInv = ownerClause("owner_id", ownerId);
        long invoices = count(s, "SELECT COUNT(*) FROM invoices WHERE deleted_at IS NULL " +
                "AND invoice_date >= :f AND invoice_date < :t" + ofInv, p);
        BigDecimal revenue = sum(s, "SELECT COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) " +
                "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                "WHERE i.deleted_at IS NULL AND i.status <> 'cancelled' " +
                "AND i.invoice_date >= :f AND i.invoice_date < :t" + ownerClause("i.owner_id", ownerId), p);
        long overdue = count(s, "SELECT COUNT(*) FROM invoices WHERE deleted_at IS NULL AND due_date IS NOT NULL " +
                "AND due_date < CURDATE() AND payment_status <> 'paid' AND status <> 'cancelled' " +
                "AND invoice_date >= :f AND invoice_date < :t" + ofInv, p);
        ctx.append("Hóa đơn: ").append(invoices)
                .append(breakdown(s, "SELECT status, COUNT(*) FROM invoices WHERE deleted_at IS NULL " +
                        "AND invoice_date >= :f AND invoice_date < :t" + ofInv + " GROUP BY status", p))
                .append(" | Doanh thu: ").append(money(revenue)).append(" đ")
                .append(" | Quá hạn: ").append(overdue).append("\n");

        // Phiếu chăm sóc
        ctx.append("Phiếu chăm sóc: ").append(countIn(s, "support_tickets", "assigned_user_id", p, ownerId))
                .append(statusBreakdown(s, "support_tickets", "assigned_user_id", p, ownerId)).append("\n");

        // Chiến dịch (kèm ngân sách)
        BigDecimal budget = sum(s, "SELECT COALESCE(SUM(budget),0) FROM campaigns WHERE deleted_at IS NULL " +
                "AND created_at >= :f AND created_at < :t" + ownerClause("owner_id", ownerId), p);
        ctx.append("Chiến dịch: ").append(countIn(s, "campaigns", "owner_id", p, ownerId))
                .append(statusBreakdown(s, "campaigns", "owner_id", p, ownerId))
                .append(" | Ngân sách: ").append(money(budget)).append(" đ\n");

        // Sản phẩm bán ra trong khoảng (qua dòng hóa đơn — invoice_items không có deleted_at)
        List<Object[]> prod = rows(s, "SELECT COUNT(DISTINCT ii.product_id), COALESCE(SUM(ii.quantity),0) " +
                "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                "WHERE i.deleted_at IS NULL AND i.status <> 'cancelled' " +
                "AND i.invoice_date >= :f AND i.invoice_date < :t" + ownerClause("i.owner_id", ownerId), p);
        if (!prod.isEmpty()) {
            Object[] r = prod.get(0);
            ctx.append("Sản phẩm bán ra: ").append(((Number) r[0]).longValue()).append(" mã, tổng số lượng ")
                    .append(money(toBig(r[1]))).append("\n");
        }
        ctx.append("\n");
    }

    // đếm bản ghi tạo trong khoảng (luôn loại bản ghi đã xóa)
    private static long countIn(Session s, String table, String ownerCol, Map<String, Object> p, Long ownerId) {
        return count(s, "SELECT COUNT(*) FROM " + table + " WHERE deleted_at IS NULL " +
                "AND created_at >= :f AND created_at < :t" + ownerClause(ownerCol, ownerId), p);
    }

    // phân rã theo cột status cho bảng lọc bằng created_at
    private static String statusBreakdown(Session s, String table, String ownerCol,
                                          Map<String, Object> p, Long ownerId) {
        return breakdown(s, "SELECT status, COUNT(*) FROM " + table + " WHERE deleted_at IS NULL " +
                "AND created_at >= :f AND created_at < :t" + ownerClause(ownerCol, ownerId) + " GROUP BY status", p);
    }

    // chạy truy vấn [nhãn, số lượng] rồi format thành " (a 1, b 2)"; rỗng → chuỗi rỗng
    private static String breakdown(Session s, String sql, Map<String, Object> p) {
        List<Object[]> rs = rows(s, sql, p);
        if (rs.isEmpty()) return "";
        List<String> parts = rs.stream().map(r -> str(r[0]) + " " + ((Number) r[1]).longValue()).toList();
        return " (" + String.join(", ", parts) + ")";
    }
}
