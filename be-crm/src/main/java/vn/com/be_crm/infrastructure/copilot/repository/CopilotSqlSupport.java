package vn.com.be_crm.infrastructure.copilot.repository;

import org.hibernate.Session;
import vn.com.be_crm.domain.dashboard.model.DateRange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// helper dùng chung cho các lớp truy vấn ngữ cảnh Copilot (native query + format số).
// Tách riêng để giữ mỗi file dưới 400 dòng theo quy ước dự án.
final class CopilotSqlSupport {

    static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    // công thức thành tiền một dòng hóa đơn (khớp LineItemTotals.lineAmount) — invoices/invoice_items
    // không còn cột total/amount lưu sẵn (tính on-read từ dòng hàng)
    static final String INVOICE_LINE_AMOUNT = "(ii.quantity * ii.unit_price - ii.discount) * (1 + ii.tax_rate / 100)";

    private CopilotSqlSupport() {
    }

    // chạy native query trả về một giá trị đếm
    static long count(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        Object r = q.uniqueResult();
        return r == null ? 0 : ((Number) r).longValue();
    }

    // chạy native query trả về một giá trị SUM
    static BigDecimal sum(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        return toBig(q.uniqueResult());
    }

    // chạy native query trả về nhiều cột
    @SuppressWarnings("unchecked")
    static List<Object[]> rows(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object[].class);
        params.forEach(q::setParameter);
        return q.list();
    }

    // map tham số {f, t} theo khoảng + owner tùy chọn
    static Map<String, Object> dateOwner(DateRange r, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", r.from());
        m.put("t", r.toExclusive());
        if (ownerId != null) m.put("o", ownerId);
        return m;
    }

    // map tham số {f, t} theo cặp ngày + owner tùy chọn
    static Map<String, Object> dateOwner(LocalDate from, LocalDate to, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", from);
        m.put("t", to);
        if (ownerId != null) m.put("o", ownerId);
        return m;
    }

    // map chỉ chứa owner
    static Map<String, Object> owner(Long ownerId) {
        return ownerId == null ? Map.of() : Map.of("o", ownerId);
    }

    // mệnh đề lọc owner cho một cột (rỗng nếu không lọc)
    static String ownerClause(String column, Long ownerId) {
        return ownerId == null ? "" : " AND " + column + " = :o";
    }

    // định dạng tiền có phân tách nghìn
    static String money(BigDecimal v) {
        return new DecimalFormat("#,###").format(v == null ? BigDecimal.ZERO : v);
    }

    // mô tả mức tăng/giảm so với kỳ trước — "+12.3%" / "-4.5%" / "mới phát sinh"
    static String growth(BigDecimal cur, BigDecimal prev) {
        if (prev == null || prev.signum() == 0) return cur.signum() == 0 ? "0%" : "mới phát sinh";
        BigDecimal pct = cur.subtract(prev).multiply(HUNDRED).divide(prev, 1, RoundingMode.HALF_UP);
        return (pct.signum() >= 0 ? "+" : "") + pct + "%";
    }

    // tỷ lệ phần trăm num/den làm tròn 1 chữ số
    static String ratePct(long num, long den) {
        if (den == 0) return "0%";
        return BigDecimal.valueOf(num).multiply(HUNDRED).divide(BigDecimal.valueOf(den), 1, RoundingMode.HALF_UP) + "%";
    }

    // ép object kết quả về BigDecimal an toàn
    static BigDecimal toBig(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        if (o instanceof Number n) return new BigDecimal(n.toString());
        return new BigDecimal(o.toString());
    }

    // ép object về String an toàn
    static String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
