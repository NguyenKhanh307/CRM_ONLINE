package vn.com.be_crm.application.copilot.intent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Đọc câu hỏi → ra khoảng thời gian cụ thể (Java thuần, không SQL, không gọi AI).
 * Hỗ trợ khoảng ngày tường minh ("từ 3/5/2025 tới 4/9/2025"), một ngày, tháng/quý/năm và mốc tương đối.
 * Nhờ đó Copilot trả lời được câu hỏi ở BẤT KỲ mốc thời gian nào, kể cả ngày lẻ.
 */
public class CopilotRangeParser {

    /**
     * Một khoảng thời gian nửa mở [from, toExclusive) kèm nhãn hiển thị cho người đọc.
     *
     * @param from        ngày bắt đầu (bao gồm)
     * @param toExclusive ngày kết thúc (KHÔNG bao gồm)
     * @param label       nhãn tiếng Việt mô tả khoảng (vd "03/05/2025 - 04/09/2025")
     */
    public record Range(LocalDate from, LocalDate toExclusive, String label) {
    }

    private static final int MAX_RANGES = 2;

    /** "từ 3/5/2025 tới 4/9/2025" — chấp nhận / hoặc - làm dấu phân cách ngày. */
    private static final Pattern EXPLICIT_RANGE = Pattern.compile(
            "tu\\s+(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})\\s*(?:toi|den|->|-)\\s*(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})");
    /** "ngày 3/5/2025" hoặc một ngày đứng riêng dạng d/M/yyyy. */
    private static final Pattern SINGLE_DAY = Pattern.compile("(?:ngay\\s+)?(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})");
    /** "tháng 5", "tháng 5/2025". */
    private static final Pattern MONTH = Pattern.compile("thang\\s+(\\d{1,2})(?:\\s*[/-]\\s*(\\d{4}))?");
    /** "quý 2", "quý 2/2025". */
    private static final Pattern QUARTER = Pattern.compile("quy\\s+([1-4])(?:\\s*[/-]\\s*(\\d{4}))?");
    /** "năm 2025". */
    private static final Pattern YEAR = Pattern.compile("nam\\s+(\\d{4})");
    /** "7 ngày qua". */
    private static final Pattern LAST_DAYS = Pattern.compile("(\\d{1,3})\\s+ngay\\s+(?:qua|gan day|truoc)");

    /**
     * Dò các khoảng thời gian được nhắc trong câu hỏi (tối đa 2 — đủ cho câu so sánh A với B).
     *
     * @param question câu hỏi gốc (có dấu)
     * @return danh sách khoảng theo thứ tự xuất hiện; rỗng nếu câu hỏi không nêu mốc nào
     */
    public List<Range> parse(String question) {
        if (question == null || question.isBlank()) return List.of();
        String q = strip(question).toLowerCase(Locale.ROOT);
        List<Range> out = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 1) Khoảng ngày tường minh — ưu tiên cao nhất
        Matcher m = EXPLICIT_RANGE.matcher(q);
        while (m.find() && out.size() < MAX_RANGES) {
            LocalDate a = toDate(num(m.group(3)), num(m.group(2)), num(m.group(1)));
            LocalDate b = toDate(num(m.group(6)), num(m.group(5)), num(m.group(4)));
            if (a != null && b != null && !b.isBefore(a)) {
                // Cận trên +1 ngày để bao trọn ngày kết thúc.
                out.add(new Range(a, b.plusDays(1), fmt(a) + " - " + fmt(b)));
            }
        }
        if (!out.isEmpty()) return out;

        // 2) Ngày đơn lẻ
        m = SINGLE_DAY.matcher(q);
        while (m.find() && out.size() < MAX_RANGES) {
            LocalDate d = toDate(num(m.group(3)), num(m.group(2)), num(m.group(1)));
            if (d != null) out.add(new Range(d, d.plusDays(1), "ngày " + fmt(d)));
        }
        if (!out.isEmpty()) return out;

        // 3) Tháng / quý / năm cụ thể
        m = MONTH.matcher(q);
        while (m.find() && out.size() < MAX_RANGES) {
            int mo = num(m.group(1));
            int yr = m.group(2) != null ? num(m.group(2)) : today.getYear();
            if (mo >= 1 && mo <= 12 && yr >= 2000) {
                LocalDate f = LocalDate.of(yr, mo, 1);
                out.add(new Range(f, f.plusMonths(1), String.format("tháng %02d/%d", mo, yr)));
            }
        }
        m = QUARTER.matcher(q);
        while (m.find() && out.size() < MAX_RANGES) {
            int qt = num(m.group(1));
            int yr = m.group(2) != null ? num(m.group(2)) : today.getYear();
            if (qt >= 1 && qt <= 4 && yr >= 2000) {
                LocalDate f = LocalDate.of(yr, (qt - 1) * 3 + 1, 1);
                out.add(new Range(f, f.plusMonths(3), "quý " + qt + "/" + yr));
            }
        }
        m = YEAR.matcher(q);
        while (m.find() && out.size() < MAX_RANGES) {
            int yr = num(m.group(1));
            if (yr >= 2000) {
                LocalDate f = LocalDate.of(yr, 1, 1);
                out.add(new Range(f, f.plusYears(1), "năm " + yr));
            }
        }
        if (!out.isEmpty()) return out;

        // 4) Mốc tương đối
        m = LAST_DAYS.matcher(q);
        if (m.find()) {
            int n = num(m.group(1));
            if (n > 0 && n <= 730) {
                LocalDate f = today.minusDays(n);
                return List.of(new Range(f, today.plusDays(1), n + " ngày qua"));
            }
        }
        if (q.contains("hom nay")) return List.of(new Range(today, today.plusDays(1), "hôm nay"));
        if (q.contains("hom qua")) {
            LocalDate y = today.minusDays(1);
            return List.of(new Range(y, today, "hôm qua"));
        }
        if (q.contains("tuan nay")) {
            LocalDate f = today.minusDays(today.getDayOfWeek().getValue() - 1L);
            return List.of(new Range(f, f.plusWeeks(1), "tuần này"));
        }
        if (q.contains("thang truoc")) {
            LocalDate f = today.withDayOfMonth(1).minusMonths(1);
            return List.of(new Range(f, f.plusMonths(1), "tháng trước"));
        }
        if (q.contains("quy truoc")) {
            LocalDate cur = LocalDate.of(today.getYear(), ((today.getMonthValue() - 1) / 3) * 3 + 1, 1);
            LocalDate f = cur.minusMonths(3);
            return List.of(new Range(f, cur, "quý trước"));
        }
        if (q.contains("nam ngoai") || q.contains("nam truoc")) {
            LocalDate f = LocalDate.of(today.getYear() - 1, 1, 1);
            return List.of(new Range(f, f.plusYears(1), "năm " + (today.getYear() - 1)));
        }
        return out;
    }

    /** Dựng LocalDate an toàn; trả null nếu giá trị vô lý. */
    private LocalDate toDate(int year, int month, int day) {
        if (year < 2000 || year > 2100 || month < 1 || month > 12 || day < 1 || day > 31) return null;
        try {
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return null;
        }
    }

    /** Parse số nguyên an toàn (trả 0 nếu lỗi). */
    private int num(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /** Format ngày dd/MM/yyyy cho nhãn hiển thị. */
    private String fmt(LocalDate d) {
        return String.format("%02d/%02d/%d", d.getDayOfMonth(), d.getMonthValue(), d.getYear());
    }

    /** Bỏ dấu tiếng Việt (giữ nguyên độ dài) để so khớp regex không lệ thuộc dấu. */
    private String strip(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c == 'đ' || c == 'Đ') { sb.append('d'); continue; }
            String d = java.text.Normalizer.normalize(String.valueOf(c), java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            sb.append(d.isEmpty() ? c : d.charAt(0));
        }
        return sb.toString();
    }
}
