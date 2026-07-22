package vn.com.be_crm.infrastructure.copilot.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.copilot.dto.RecordRef;
import vn.com.be_crm.application.copilot.intent.CopilotRangeParser;
import vn.com.be_crm.application.copilot.intent.CopilotRangeParser.Range;
import vn.com.be_crm.application.dashboard.query.PeriodRanges;
import vn.com.be_crm.domain.copilot.repository.ICopilotContextRepository;
import vn.com.be_crm.domain.dashboard.model.DateRange;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static vn.com.be_crm.infrastructure.copilot.repository.CopilotSqlSupport.*;

/**
 * Điều phối việc gom ngữ cảnh CRM cho trợ lý AI: số liệu tổng hợp, chuỗi 24 tháng, xếp hạng,
 * đếm theo phân hệ, khối theo khoảng thời gian được hỏi, và phễu bản ghi cụ thể.
 * <p>SQL chi tiết nằm ở {@link CopilotAnalyticsQueries} / {@link CopilotRangeQueries};
 * helper dùng chung ở {@link CopilotSqlSupport}. Mọi truy vấn loại bản ghi đã xóa mềm
 * (kể cả trong Thùng rác) bằng {@code deleted_at IS NULL}.
 */
@Repository
public class CopilotContextRepositoryImpl implements ICopilotContextRepository {

    /** Từ dừng tiếng Việt thường gặp — loại khi phân giải tên/mã bản ghi. */
    private static final Set<String> STOPWORDS = Set.of(
            "doanh", "thu", "quy", "quý", "thang", "tháng", "nam", "năm", "tuan", "tuần", "nay", "này",
            "khach", "khách", "hang", "hàng", "bao", "nhieu", "nhiêu", "ty", "le", "tỷ", "lệ",
            "thắng", "chot", "chốt", "don", "đơn", "san", "pham", "sản", "phẩm",
            "co", "hoi", "cơ", "hội", "hoa", "hóa", "cua", "của", "cho", "toi", "tôi", "xem",
            "la", "là", "gi", "gì", "the", "thế", "nao", "nào", "va", "và",
            "tong", "tổng", "so", "số", "luong", "lượng", "hien", "hiện", "tai", "tại", "dang", "đang",
            "phan", "tich", "phân", "tích", "thong", "ke", "thống", "kê", "hom", "hôm", "gio", "giờ",
            "moi", "mới", "cu", "cũ", "truoc", "trước", "sau", "voi", "với", "trong", "ngoai", "ngoài");

    private final SessionFactory sf;
    private final CopilotRangeParser rangeParser = new CopilotRangeParser();

    /** @param sf Hibernate SessionFactory */
    public CopilotContextRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    public String assemble(String question, Long ownerId, boolean isPrivileged) {
        List<Range> ranges = rangeParser.parse(question);
        String period = detectPeriod(question);
        DateRange cur = PeriodRanges.current(period);

        return TxSupport.read(sf, s -> {
            StringBuilder ctx = new StringBuilder();
            appendAggregates(s, ctx, period, cur, ownerId);
            // Khối cho từng khoảng thời gian được hỏi (ngày lẻ, tháng, quý, năm... đều đúng).
            for (Range r : ranges) {
                CopilotRangeQueries.appendRangeBlock(s, ctx, r.from(), r.toExclusive(), r.label(), ownerId);
            }
            CopilotAnalyticsQueries.appendMonthlySeries(s, ctx, ownerId);
            // Xếp hạng bám theo khoảng được hỏi (nếu có), ngược lại dùng kỳ mặc định.
            Range top = ranges.isEmpty() ? null : ranges.get(0);
            if (top != null) {
                CopilotAnalyticsQueries.appendRankings(s, ctx, top.from(), top.toExclusive(),
                        top.label(), ownerId, isPrivileged);
            } else {
                CopilotAnalyticsQueries.appendRankings(s, ctx, cur.from(), cur.toExclusive(),
                        periodLabel(period), ownerId, isPrivileged);
            }
            CopilotAnalyticsQueries.appendModuleCounts(s, ctx, cur, periodLabel(period), ownerId);
            appendRecords(s, ctx, question, ownerId, isPrivileged);
            return ctx.toString();
        });
    }

    /** Cấu hình truy vấn tìm bản ghi theo module: bảng, cột tên, cột mã (null nếu không có), cột LIKE, cột owner. */
    private record ModuleQuery(String table, String nameCol, String codeCol, List<String> likeCols, String ownerCol) {
    }

    private static final Map<String, ModuleQuery> RECORD_QUERIES = Map.of(
            "customer", new ModuleQuery("customers", "name", "code", List.of("name", "code", "tax_code"), "owner_id"),
            "lead", new ModuleQuery("leads", "name", "code", List.of("name", "company_name", "code"), "owner_id"),
            "contact", new ModuleQuery("contacts", "full_name", null, List.of("full_name", "email"), "assigned_user_id"),
            "opportunity", new ModuleQuery("opportunities", "name", "code", List.of("name", "code"), "owner_id"),
            "quotation", new ModuleQuery("quotations", "code", "code", List.of("code"), "owner_id"),
            "order", new ModuleQuery("orders", "code", "code", List.of("code"), "owner_id"),
            "invoice", new ModuleQuery("invoices", "code", "code", List.of("code"), "owner_id"),
            "campaign", new ModuleQuery("campaigns", "name", "code", List.of("name", "code"), "owner_id"),
            "ticket", new ModuleQuery("support_tickets", "subject", "code", List.of("subject", "code"), "assigned_user_id"));

    /** {@inheritDoc} */
    @Override
    public Optional<RecordRef> findRecord(String module, String term, Long ownerId, boolean isPrivileged) {
        ModuleQuery mq = RECORD_QUERIES.get(module);
        if (mq == null || term == null || term.isBlank()) return Optional.empty();
        String like = mq.likeCols().stream().map(c -> c + " LIKE :t").reduce((a, b) -> a + " OR " + b).orElse("1=0");
        String ownerFilter = (ownerId == null || isPrivileged) ? "" : " AND " + mq.ownerCol() + " = :o";
        String codeSel = mq.codeCol() == null ? "NULL" : mq.codeCol();
        String sql = "SELECT id, " + codeSel + ", " + mq.nameCol() + " FROM " + mq.table() +
                " WHERE deleted_at IS NULL AND (" + like + ")" + ownerFilter +
                " ORDER BY updated_at DESC LIMIT 1";
        return TxSupport.read(sf, s -> {
            Map<String, Object> params = new HashMap<>();
            params.put("t", "%" + term.trim() + "%");
            if (!ownerFilter.isEmpty()) params.put("o", ownerId);
            List<Object[]> rs = rows(s, sql, params);
            if (rs.isEmpty()) return Optional.empty();
            Object[] r = rs.get(0);
            return Optional.of(new RecordRef(((Number) r[0]).longValue(),
                    r[1] == null ? null : r[1].toString(), str(r[2])));
        });
    }

    // ==================== Khối SỐ LIỆU TỔNG HỢP ====================

    /** Dựng khối số liệu tổng hợp: doanh thu so kỳ, đếm nhanh, tỷ lệ thắng/chốt đơn, phễu. */
    private void appendAggregates(Session s, StringBuilder ctx, String period, DateRange cur, Long ownerId) {
        String label = periodLabel(period);
        DateRange prev = PeriodRanges.previous(period);
        String of = ownerClause("owner_id", ownerId);

        BigDecimal revCur = sum(s, "SELECT COALESCE(SUM(total),0) FROM invoices WHERE status <> 'cancelled' " +
                "AND deleted_at IS NULL AND invoice_date >= :f AND invoice_date < :t" + of, dateOwner(cur, ownerId));
        BigDecimal revPrev = sum(s, "SELECT COALESCE(SUM(total),0) FROM invoices WHERE status <> 'cancelled' " +
                "AND deleted_at IS NULL AND invoice_date >= :f AND invoice_date < :t" + of, dateOwner(prev, ownerId));

        long oppOpen = count(s, "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL AND status = 'open'" + of, owner(ownerId));
        long overdueInv = count(s, "SELECT COUNT(*) FROM invoices WHERE deleted_at IS NULL AND due_date IS NOT NULL " +
                "AND due_date < CURDATE() AND payment_status <> 'paid' AND status <> 'cancelled'" + of, owner(ownerId));
        long won = count(s, "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL AND status = 'won'" + of, owner(ownerId));
        long lost = count(s, "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL AND status = 'lost'" + of, owner(ownerId));
        long totalOpp = count(s, "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL" + of, owner(ownerId));
        long oppWithOrder = count(s, "SELECT COUNT(DISTINCT opportunity_id) FROM orders WHERE deleted_at IS NULL " +
                "AND opportunity_id IS NOT NULL" + of, owner(ownerId));

        ctx.append("=== SỐ LIỆU TỔNG HỢP (").append(label).append(", do hệ thống tính từ CSDL) ===\n");
        ctx.append("Doanh thu ").append(label).append(": ").append(money(revCur)).append(" đ")
                .append(" | Kỳ trước: ").append(money(revPrev)).append(" đ")
                .append(" | Thay đổi: ").append(growth(revCur, revPrev)).append("\n");
        ctx.append("Cơ hội đang mở: ").append(oppOpen).append(" | Hóa đơn quá hạn: ").append(overdueInv).append("\n");
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

    // ==================== Khối BẢN GHI CỤ THỂ ====================

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

    /** Suy mã kỳ mặc định từ từ khóa trong câu hỏi (mặc định quarter). */
    private String detectPeriod(String q) {
        String low = q == null ? "" : q.toLowerCase();
        if (low.contains("tháng") || low.contains("thang")) return "month";
        if (low.contains("năm") || low.contains("nam")) return "year";
        return "quarter";
    }

    /** Nhãn tiếng Việt của mã kỳ. */
    private String periodLabel(String period) {
        return switch (period) {
            case "month" -> "tháng này";
            case "year" -> "năm nay";
            default -> "quý này";
        };
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
}
