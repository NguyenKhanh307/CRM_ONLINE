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

/**
 * Helper dùng chung cho các lớp truy vấn ngữ cảnh Copilot (native query + format số).
 * Tách riêng để giữ mỗi file dưới 400 dòng theo quy ước dự án.
 */
final class CopilotSqlSupport {

    static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private CopilotSqlSupport() {
    }

    /**
     * Chạy native query trả về một giá trị đếm.
     *
     * @param s      session Hibernate
     * @param sql    câu SQL
     * @param params tham số đặt tên
     * @return giá trị đếm (0 nếu null)
     */
    static long count(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        Object r = q.uniqueResult();
        return r == null ? 0 : ((Number) r).longValue();
    }

    /**
     * Chạy native query trả về một giá trị SUM.
     *
     * @param s      session Hibernate
     * @param sql    câu SQL
     * @param params tham số đặt tên
     * @return tổng dạng BigDecimal (0 nếu null)
     */
    static BigDecimal sum(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        return toBig(q.uniqueResult());
    }

    /**
     * Chạy native query trả về nhiều cột.
     *
     * @param s      session Hibernate
     * @param sql    câu SQL
     * @param params tham số đặt tên
     * @return danh sách dòng kết quả
     */
    @SuppressWarnings("unchecked")
    static List<Object[]> rows(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object[].class);
        params.forEach(q::setParameter);
        return q.list();
    }

    /**
     * Map tham số {f, t} theo khoảng + owner tùy chọn.
     *
     * @param r       khoảng thời gian
     * @param ownerId id người phụ trách (null = bỏ qua)
     * @return map tham số
     */
    static Map<String, Object> dateOwner(DateRange r, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", r.from());
        m.put("t", r.toExclusive());
        if (ownerId != null) m.put("o", ownerId);
        return m;
    }

    /**
     * Map tham số {f, t} theo cặp ngày + owner tùy chọn.
     *
     * @param from    mốc bắt đầu
     * @param to      mốc kết thúc (không bao gồm)
     * @param ownerId id người phụ trách (null = bỏ qua)
     * @return map tham số
     */
    static Map<String, Object> dateOwner(LocalDate from, LocalDate to, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", from);
        m.put("t", to);
        if (ownerId != null) m.put("o", ownerId);
        return m;
    }

    /**
     * Map chỉ chứa owner.
     *
     * @param ownerId id người phụ trách (null = map rỗng)
     * @return map tham số
     */
    static Map<String, Object> owner(Long ownerId) {
        return ownerId == null ? Map.of() : Map.of("o", ownerId);
    }

    /**
     * Mệnh đề lọc owner cho một cột (rỗng nếu không lọc).
     *
     * @param column  tên cột owner (có thể kèm alias)
     * @param ownerId id người phụ trách
     * @return chuỗi " AND col = :o" hoặc ""
     */
    static String ownerClause(String column, Long ownerId) {
        return ownerId == null ? "" : " AND " + column + " = :o";
    }

    /**
     * Định dạng tiền có phân tách nghìn.
     *
     * @param v giá trị
     * @return chuỗi đã format
     */
    static String money(BigDecimal v) {
        return new DecimalFormat("#,###").format(v == null ? BigDecimal.ZERO : v);
    }

    /**
     * Mô tả mức tăng/giảm so với kỳ trước.
     *
     * @param cur  giá trị kỳ này
     * @param prev giá trị kỳ trước
     * @return chuỗi "+12.3%" / "-4.5%" / "mới phát sinh"
     */
    static String growth(BigDecimal cur, BigDecimal prev) {
        if (prev == null || prev.signum() == 0) return cur.signum() == 0 ? "0%" : "mới phát sinh";
        BigDecimal pct = cur.subtract(prev).multiply(HUNDRED).divide(prev, 1, RoundingMode.HALF_UP);
        return (pct.signum() >= 0 ? "+" : "") + pct + "%";
    }

    /**
     * Tỷ lệ phần trăm num/den làm tròn 1 chữ số.
     *
     * @param num tử số
     * @param den mẫu số
     * @return chuỗi phần trăm
     */
    static String ratePct(long num, long den) {
        if (den == 0) return "0%";
        return BigDecimal.valueOf(num).multiply(HUNDRED).divide(BigDecimal.valueOf(den), 1, RoundingMode.HALF_UP) + "%";
    }

    /**
     * Ép object kết quả về BigDecimal an toàn.
     *
     * @param o giá trị thô
     * @return BigDecimal (0 nếu null)
     */
    static BigDecimal toBig(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        if (o instanceof Number n) return new BigDecimal(n.toString());
        return new BigDecimal(o.toString());
    }

    /**
     * Ép object về String an toàn.
     *
     * @param o giá trị thô
     * @return chuỗi ("" nếu null)
     */
    static String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
