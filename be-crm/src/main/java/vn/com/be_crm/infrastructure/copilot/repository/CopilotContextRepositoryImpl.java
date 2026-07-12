package vn.com.be_crm.infrastructure.copilot.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.dashboard.query.PeriodRanges;
import vn.com.be_crm.domain.copilot.repository.ICopilotContextRepository;
import vn.com.be_crm.domain.dashboard.model.DateRange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Gom ngữ cảnh CRM cho trợ lý AI bằng native SQL (mẫu từ DashboardRepositoryImpl).
 * Gồm khối SỐ LIỆU TỔNG HỢP (luôn có) và khối BẢN GHI CỤ THỂ (khi câu hỏi nhắc tên/mã khách hàng).
 */
@Repository
public class CopilotContextRepositoryImpl implements ICopilotContextRepository {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final DecimalFormat MONEY = new DecimalFormat("#,###");

    /** Từ dừng tiếng Việt thường gặp trong câu hỏi CRM — loại khi phân giải tên/mã bản ghi. */
    private static final Set<String> STOPWORDS = Set.of(
            "doanh", "thu", "quy", "quý", "thang", "tháng", "nam", "năm", "tuan", "tuần", "nay", "này",
            "khach", "khách", "hang", "hàng", "bao", "nhieu", "nhiêu", "ty", "le", "tỷ", "lệ",
            "thang(win)", "thắng", "chot", "chốt", "don", "đơn", "san", "pham", "sản", "phẩm",
            "co", "hoi", "cơ", "hội", "hoa", "hóa", "cua", "của", "cho", "toi", "tôi", "xem",
            "la", "là", "gi", "gì", "the", "thế", "nao", "nào", "va", "và", "co-hoi", "bao-gia",
            "tong", "tổng", "so", "số", "luong", "lượng", "hien", "hiện", "tai", "tại", "dang", "đang",
            "phan", "tich", "phân", "tích", "thong", "ke", "thống", "kê", "hom", "hôm", "gio", "giờ",
            "moi", "mới", "cu", "cũ", "truoc", "trước", "sau", "voi", "với", "trong", "ngoai", "ngoài");

    private final SessionFactory sf;

    /** @param sf Hibernate SessionFactory */
    public CopilotContextRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    public String assemble(String question, Long ownerId, boolean isPrivileged) {
        return TxSupport.read(sf, s -> {
            StringBuilder ctx = new StringBuilder();
            appendAggregates(s, ctx, question, ownerId);
            appendRecords(s, ctx, question, ownerId, isPrivileged);
            return ctx.toString();
        });
    }

    // ==================== Khối SỐ LIỆU TỔNG HỢP (Loại A) ====================

    /** Dựng khối số liệu tổng hợp: doanh thu so kỳ, đếm, tỷ lệ thắng/chốt đơn, phễu. */
    private void appendAggregates(Session s, StringBuilder ctx, String question, Long ownerId) {
        String period = detectPeriod(question);
        String periodLabel = switch (period) {
            case "month" -> "tháng này"; case "year" -> "năm nay"; default -> "quý này";
        };
        DateRange cur = PeriodRanges.current(period);
        DateRange prev = PeriodRanges.previous(period);
        String of = ownerId == null ? "" : " AND owner_id = :o";

        BigDecimal revCur = sum(s, "SELECT COALESCE(SUM(total),0) FROM invoices WHERE status <> 'cancelled' " +
                "AND deleted_at IS NULL AND invoice_date >= :f AND invoice_date < :t" + of, dateOwner(cur, ownerId));
        BigDecimal revPrev = sum(s, "SELECT COALESCE(SUM(total),0) FROM invoices WHERE status <> 'cancelled' " +
                "AND deleted_at IS NULL AND invoice_date >= :f AND invoice_date < :t" + of, dateOwner(prev, ownerId));

        long customers = count(s, "SELECT COUNT(*) FROM customers WHERE deleted_at IS NULL" + of, owner(ownerId));
        long products = count(s, "SELECT COUNT(*) FROM products WHERE deleted_at IS NULL", Map.of());
        long oppOpen = count(s, "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL AND status = 'open'" + of, owner(ownerId));
        long overdueInv = count(s, "SELECT COUNT(*) FROM invoices WHERE deleted_at IS NULL AND due_date IS NOT NULL " +
                "AND due_date < CURDATE() AND payment_status <> 'paid' AND status <> 'cancelled'" + of, owner(ownerId));

        long won = count(s, "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL AND status = 'won'" + of, owner(ownerId));
        long lost = count(s, "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL AND status = 'lost'" + of, owner(ownerId));
        long totalOpp = count(s, "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL" + of, owner(ownerId));
        long oppWithOrder = count(s, "SELECT COUNT(DISTINCT opportunity_id) FROM orders WHERE deleted_at IS NULL " +
                "AND opportunity_id IS NOT NULL" + of, owner(ownerId));

        ctx.append("=== SỐ LIỆU TỔNG HỢP (").append(periodLabel).append(", do hệ thống tính từ CSDL) ===\n");
        ctx.append("Doanh thu ").append(periodLabel).append(": ").append(money(revCur)).append(" đ")
                .append(" | Kỳ trước: ").append(money(revPrev)).append(" đ")
                .append(" | Thay đổi: ").append(growth(revCur, revPrev)).append("\n");
        ctx.append("Số khách hàng: ").append(customers).append(" | Số sản phẩm: ").append(products)
                .append(" | Cơ hội đang mở: ").append(oppOpen).append(" | Hóa đơn quá hạn: ").append(overdueInv).append("\n");
        ctx.append("Tỷ lệ thắng (won/(won+lost)): ").append(ratePct(won, won + lost))
                .append(" (thắng ").append(won).append(", thua ").append(lost).append(")\n");
        ctx.append("Tỷ lệ chốt đơn (cơ hội có đơn hàng/tổng cơ hội): ").append(ratePct(oppWithOrder, totalOpp))
                .append(" (").append(oppWithOrder).append("/").append(totalOpp).append(")\n");

        List<Object[]> funnel = rows(s, "SELECT st.name, COUNT(o.id) FROM opportunity_stages st " +
                "LEFT JOIN opportunities o ON o.stage_id = st.id AND o.deleted_at IS NULL" +
                (ownerId == null ? "" : " AND o.owner_id = :o") +
                " GROUP BY st.id, st.name, st.sort_order ORDER BY st.sort_order", owner(ownerId));
        if (!funnel.isEmpty()) {
            ctx.append("Phễu theo giai đoạn: ");
            List<String> parts = new ArrayList<>();
            for (Object[] r : funnel) parts.add(str(r[0]) + "=" + ((Number) r[1]).longValue());
            ctx.append(String.join(", ", parts)).append("\n");
        }
        ctx.append("\n");
    }

    // ==================== Khối BẢN GHI CỤ THỂ (Loại B) ====================

    /** Nếu câu hỏi nhắc tên/mã khách hàng, phân giải rồi gom phễu của từng khách khớp. */
    private void appendRecords(Session s, StringBuilder ctx, String question, Long ownerId, boolean isPrivileged) {
        List<String> terms = extractTerms(question);
        if (terms.isEmpty()) return;

        String like = terms.stream().map(t -> "name LIKE :t" + terms.indexOf(t)).reduce((a, b) -> a + " OR " + b).orElse("");
        String likeCode = terms.stream().map(t -> "code LIKE :t" + terms.indexOf(t) + " OR tax_code LIKE :t" + terms.indexOf(t))
                .reduce((a, b) -> a + " OR " + b).orElse("");
        String ofC = (ownerId == null || isPrivileged) ? "" : " AND owner_id = :o";
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < terms.size(); i++) params.put("t" + i, "%" + terms.get(i) + "%");
        if (!ofC.isEmpty()) params.put("o", ownerId);

        List<Object[]> matched = rows(s, "SELECT id, code, name FROM customers WHERE deleted_at IS NULL AND ((" +
                like + ") OR (" + likeCode + "))" + ofC + " ORDER BY updated_at DESC LIMIT 3", params);
        if (matched.isEmpty()) return;

        ctx.append("=== BẢN GHI LIÊN QUAN CÂU HỎI ===\n");
        for (Object[] c : matched) {
            long cid = ((Number) c[0]).longValue();
            ctx.append("Khách hàng: ").append(str(c[2])).append(" (mã ").append(str(c[1])).append(")\n");
            appendCustomerFunnel(s, ctx, cid);
            ctx.append("\n");
        }
    }

    /** Gom phễu của một khách hàng: cơ hội → báo giá → đơn → hóa đơn → phiếu chăm sóc (mỗi loại tối đa 5). */
    private void appendCustomerFunnel(Session s, StringBuilder ctx, long customerId) {
        Map<String, Object> p = Map.of("c", customerId);
        for (Object[] r : rows(s, "SELECT o.code, st.name, o.status, o.amount FROM opportunities o " +
                "LEFT JOIN opportunity_stages st ON st.id = o.stage_id " +
                "WHERE o.deleted_at IS NULL AND o.customer_id = :c ORDER BY o.created_at DESC LIMIT 5", p)) {
            ctx.append("  - Cơ hội ").append(str(r[0])).append(": giai đoạn ").append(str(r[1]))
                    .append(", trạng thái ").append(str(r[2])).append(", giá trị ").append(money(toBig(r[3]))).append(" đ\n");
        }
        for (Object[] r : rows(s, "SELECT code, status, total FROM quotations WHERE deleted_at IS NULL " +
                "AND customer_id = :c ORDER BY created_at DESC LIMIT 5", p)) {
            ctx.append("  - Báo giá ").append(str(r[0])).append(": trạng thái ").append(str(r[1]))
                    .append(", tổng ").append(money(toBig(r[2]))).append(" đ\n");
        }
        for (Object[] r : rows(s, "SELECT code, status, total FROM orders WHERE deleted_at IS NULL " +
                "AND customer_id = :c ORDER BY created_at DESC LIMIT 5", p)) {
            ctx.append("  - Đơn hàng ").append(str(r[0])).append(": trạng thái ").append(str(r[1]))
                    .append(", tổng ").append(money(toBig(r[2]))).append(" đ\n");
        }
        for (Object[] r : rows(s, "SELECT code, status, total, payment_status, " +
                "(due_date IS NOT NULL AND due_date < CURDATE() AND payment_status <> 'paid' AND status <> 'cancelled') AS overdue " +
                "FROM invoices WHERE deleted_at IS NULL AND customer_id = :c ORDER BY created_at DESC LIMIT 5", p)) {
            boolean overdue = r[4] != null && ((Number) r[4]).intValue() == 1;
            ctx.append("  - Hóa đơn ").append(str(r[0])).append(": trạng thái ").append(str(r[1]))
                    .append(", tổng ").append(money(toBig(r[2]))).append(" đ, thanh toán ").append(str(r[3]))
                    .append(overdue ? " [QUÁ HẠN]" : "").append("\n");
        }
        for (Object[] r : rows(s, "SELECT code, type, status, subject FROM support_tickets WHERE deleted_at IS NULL " +
                "AND customer_id = :c ORDER BY created_at DESC LIMIT 5", p)) {
            ctx.append("  - Phiếu chăm sóc ").append(str(r[0])).append(" (").append(str(r[1])).append("): trạng thái ")
                    .append(str(r[2])).append(", nội dung: ").append(str(r[3])).append("\n");
        }
    }

    // ==================== Helpers ====================

    /** Suy mã kỳ từ từ khóa trong câu hỏi (mặc định quarter). */
    private String detectPeriod(String q) {
        String low = q == null ? "" : q.toLowerCase();
        if (low.contains("tháng") || low.contains("thang")) return "month";
        if (low.contains("năm") || low.contains("nam")) return "year";
        return "quarter";
    }

    /** Tách token (độ dài ≥ 3, loại từ dừng) để phân giải tên/mã bản ghi. */
    private List<String> extractTerms(String q) {
        if (q == null) return List.of();
        String[] raw = q.split("[^\\p{L}\\p{Nd}]+");
        List<String> out = new ArrayList<>();
        for (String w : raw) {
            String t = w.trim();
            if (t.length() >= 3 && !STOPWORDS.contains(t.toLowerCase()) && !out.contains(t)) out.add(t);
            if (out.size() >= 5) break;
        }
        return out;
    }

    /** Định dạng số tiền có phân tách nghìn. */
    private String money(BigDecimal v) {
        return MONEY.format(v == null ? BigDecimal.ZERO : v);
    }

    /** Chuỗi mô tả tăng/giảm so kỳ trước. */
    private String growth(BigDecimal cur, BigDecimal prev) {
        if (prev == null || prev.signum() == 0) return cur.signum() == 0 ? "0%" : "mới phát sinh";
        BigDecimal pct = cur.subtract(prev).multiply(HUNDRED).divide(prev, 1, RoundingMode.HALF_UP);
        return (pct.signum() >= 0 ? "+" : "") + pct + "%";
    }

    /** Tỷ lệ phần trăm num/den, làm tròn 1 chữ số. */
    private String ratePct(long num, long den) {
        if (den == 0) return "0%";
        return BigDecimal.valueOf(num).multiply(HUNDRED).divide(BigDecimal.valueOf(den), 1, RoundingMode.HALF_UP) + "%";
    }

    /** Chạy native query đếm. */
    private long count(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        Object r = q.uniqueResult();
        return r == null ? 0 : ((Number) r).longValue();
    }

    /** Chạy native query SUM (BigDecimal). */
    private BigDecimal sum(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        return toBig(q.uniqueResult());
    }

    /** Chạy native query nhiều cột. */
    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object[].class);
        params.forEach(q::setParameter);
        return q.list();
    }

    /** Map {f,t} + owner tùy chọn. */
    private Map<String, Object> dateOwner(DateRange r, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", r.from());
        m.put("t", r.toExclusive());
        if (ownerId != null) m.put("o", ownerId);
        return m;
    }

    /** Map chỉ owner (rỗng nếu null). */
    private Map<String, Object> owner(Long ownerId) {
        return ownerId == null ? Map.of() : Map.of("o", ownerId);
    }

    /** Ép về BigDecimal an toàn. */
    private BigDecimal toBig(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        if (o instanceof Number n) return new BigDecimal(n.toString());
        return new BigDecimal(o.toString());
    }

    /** Ép về String an toàn. */
    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
