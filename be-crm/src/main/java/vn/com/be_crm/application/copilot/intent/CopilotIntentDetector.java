package vn.com.be_crm.application.copilot.intent;

import java.util.List;
import java.util.Locale;

/**
 * Dò ý định lệnh trong câu chat (thuần Java, không gọi AI): mở trang, tạo mới, mở bản ghi cụ thể,
 * và cờ "muốn xem biểu đồ so sánh". So khớp trên chuỗi đã bỏ dấu nhưng term tìm kiếm giữ nguyên
 * dấu gốc (để LIKE khớp dữ liệu tiếng Việt trong DB).
 */
public class CopilotIntentDetector {

    /** Loại ý định dò được. */
    public enum Type { OPEN_PAGE, CREATE, OPEN_RECORD, NONE }

    /**
     * Kết quả dò ý định.
     *
     * @param type       loại ý định (NONE = câu hỏi thường, đi luồng RAG)
     * @param module     khóa module (customer/lead/...) — null nếu NONE
     * @param route      route FE của module — null nếu NONE
     * @param label      nhãn tiếng Việt của module — null nếu NONE
     * @param term       cụm tên/mã cần tìm (giữ nguyên dấu) — chỉ có với OPEN_RECORD
     * @param wantsChart true nếu câu hỏi mang ý so sánh/số liệu → đính link trang phân tích
     * @param period     kỳ suy từ câu hỏi (month/quarter/year)
     */
    public record Intent(Type type, String module, String route, String label,
                         String term, boolean wantsChart, String period) {
    }

    /** Một module điều hướng được: khóa, route FE, nhãn, alias (đã bỏ dấu), có trang chi tiết, có form thêm mới. */
    private record Module(String key, String route, String label, List<String> aliases,
                          boolean hasDetail, boolean hasCreate) {
    }

    private static final List<Module> MODULES = List.of(
            new Module("customer", "/khach-hang", "khách hàng", List.of("khach hang"), true, true),
            new Module("lead", "/tiem-nang", "tiềm năng", List.of("tiem nang", "lead"), true, true),
            new Module("contact", "/lien-he", "liên hệ", List.of("lien he"), true, true),
            new Module("opportunity", "/co-hoi", "cơ hội", List.of("co hoi"), true, true),
            new Module("quotation", "/bao-gia", "báo giá", List.of("bao gia"), true, true),
            new Module("order", "/don-hang", "đơn hàng", List.of("don hang"), true, true),
            new Module("invoice", "/hoa-don", "hóa đơn", List.of("hoa don"), true, true),
            new Module("campaign", "/chien-dich", "chiến dịch", List.of("chien dich"), true, true),
            new Module("ticket", "/cham-soc", "phiếu chăm sóc", List.of("cham soc", "phieu cham soc", "ticket"), true, true),
            new Module("activity", "/hoat-dong", "hoạt động", List.of("hoat dong"), false, true),
            new Module("product", "/san-pham", "sản phẩm", List.of("san pham"), false, true),
            new Module("pricing", "/chinh-sach-gia", "chính sách giá", List.of("chinh sach gia"), false, false),
            new Module("dashboard", "/dashboard", "bàn làm việc", List.of("dashboard", "ban lam viec"), false, false),
            new Module("analytics", "/phan-tich", "phân tích so sánh", List.of("phan tich", "trang so sanh"), false, false),
            new Module("trash", "/thung-rac", "thùng rác", List.of("thung rac"), false, false));

    private static final List<String> OPEN_VERBS = List.of("mo ", "vao ", "xem ", "chuyen ", "den ", "toi ", "di den ");
    private static final List<String> CREATE_VERBS = List.of("tao ", "them ");
    private static final List<String> FILLERS = List.of("trang", "danh sach", "cho toi", "giup toi", "gium toi",
            "ho toi", "moi", "cua toi", "di", "nhe", "nha", "may", "cai", "ra");
    private static final List<String> QUESTION_WORDS = List.of("nao", "bao nhieu", "gi", "the nao", "khong", "?");
    private static final List<String> CHART_WORDS = List.of("so sanh", "doanh thu", "loi nhuan", "chi phi",
            "ty le", "bieu do", "pheu", "thong ke", "tang truong");

    /**
     * Dò ý định từ câu người dùng nhập.
     *
     * @param question câu gốc (giữ dấu)
     * @return Intent — type NONE nếu không phải lệnh (kèm cờ wantsChart/period cho luồng RAG)
     */
    public Intent detect(String question) {
        String original = question == null ? "" : question.trim();
        String norm = stripDiacritics(original).toLowerCase(Locale.ROOT);
        boolean wantsChart = CHART_WORDS.stream().anyMatch(norm::contains);
        String period = detectPeriod(norm);

        boolean isCreate = startsWithAny(norm, CREATE_VERBS);
        boolean isOpen = startsWithAny(norm, OPEN_VERBS);
        if (!isCreate && !isOpen) return new Intent(Type.NONE, null, null, null, null, wantsChart, period);

        for (Module m : MODULES) {
            for (String alias : m.aliases()) {
                int idx = norm.indexOf(alias);
                if (idx < 0) continue;
                String rest = original.substring(Math.min(idx + alias.length(), original.length())).trim();
                String restNorm = cleanFillers(stripDiacritics(rest).toLowerCase(Locale.ROOT));
                if (isCreate) {
                    if (!m.hasCreate()) break;
                    return new Intent(Type.CREATE, m.key(), m.route(), m.label(), null, wantsChart, period);
                }
                // Câu hỏi trá hình ("xem khách hàng nào...") → không phải lệnh
                if (QUESTION_WORDS.stream().anyMatch(restNorm::contains)) break;
                if (restNorm.isBlank()) {
                    return new Intent(Type.OPEN_PAGE, m.key(), m.route(), m.label(), null, wantsChart, period);
                }
                if (m.hasDetail()) {
                    return new Intent(Type.OPEN_RECORD, m.key(), m.route(), m.label(),
                            cleanTerm(rest), wantsChart, period);
                }
                return new Intent(Type.OPEN_PAGE, m.key(), m.route(), m.label(), null, wantsChart, period);
            }
        }
        return new Intent(Type.NONE, null, null, null, null, wantsChart, period);
    }

    /** Suy mã kỳ từ từ khóa (mặc định quarter). @param norm chuỗi đã bỏ dấu @return month/quarter/year */
    private String detectPeriod(String norm) {
        if (norm.contains("thang")) return "month";
        if (norm.contains("nam")) return "year";
        return "quarter";
    }

    /** Kiểm tra chuỗi bắt đầu bằng một trong các tiền tố (bỏ qua từ lịch sự đứng đầu). */
    private boolean startsWithAny(String norm, List<String> verbs) {
        String s = norm;
        for (String polite : List.of("hay ", "vui long ", "lam on ", "giup toi ", "cho toi ")) {
            if (s.startsWith(polite)) s = s.substring(polite.length());
        }
        for (String v : verbs) {
            if (s.startsWith(v)) return true;
        }
        return false;
    }

    /** Xóa từ đệm khỏi chuỗi đã bỏ dấu để kiểm tra phần dư có rỗng không. */
    private String cleanFillers(String s) {
        String out = s;
        for (String f : FILLERS) out = out.replace(f, " ");
        return out.replaceAll("[^\\p{L}\\p{Nd}]+", " ").trim();
    }

    /** Làm gọn term tìm kiếm (giữ dấu): bỏ ký tự thừa đầu/cuối. */
    private String cleanTerm(String s) {
        return s.replaceAll("^[\\s:,.\"-]+|[\\s:,.\"?!-]+$", "").trim();
    }

    /**
     * Bỏ dấu tiếng Việt giữ nguyên độ dài chuỗi (map 1-1 từng ký tự) để index khớp với chuỗi gốc.
     *
     * @param s chuỗi gốc
     * @return chuỗi không dấu cùng độ dài
     */
    private String stripDiacritics(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c == 'đ') { sb.append('d'); continue; }
            if (c == 'Đ') { sb.append('D'); continue; }
            String d = java.text.Normalizer.normalize(String.valueOf(c), java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            sb.append(d.isEmpty() ? c : d.charAt(0));
        }
        return sb.toString();
    }
}
