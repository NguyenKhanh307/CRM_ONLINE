package vn.com.be_crm.core.util;

/**
 * Kiểm tra định dạng field khi nhập file Excel/CSV — dùng trong vòng lặp validate của
 * {@code ImportBulk*UseCase} (khác {@link CrossFieldRules}: KHÔNG throw, chỉ trả message hoặc
 * {@code null} để caller tự quyết định gộp vào danh sách lỗi từng dòng).
 * Regex + thông điệp khớp {@code fe-crm/src/shared/utils/validators/{email,phone,taxCode}.ts}
 * để 2 tầng đồng nhất.
 */
public final class ImportValidators {

    private ImportValidators() {}

    private static final String EMAIL_RE = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final String PHONE_RE = "^[0-9+.() -]{8,15}$";
    private static final String TAX_CODE_RE = "^[0-9-]{10,14}$";

    /**
     * Kiểm tra định dạng email — bỏ qua nếu rỗng (dùng {@code requiredError} riêng cho bắt buộc).
     *
     * @param v giá trị ô email
     * @return message lỗi hoặc {@code null}
     */
    public static String emailError(String v) {
        if (v == null || v.isBlank()) return null;
        return v.trim().matches(EMAIL_RE) ? null : "Email không hợp lệ";
    }

    /**
     * Kiểm tra định dạng số điện thoại (8-15 ký tự số, cho phép + . ( ) - và khoảng trắng).
     *
     * @param v giá trị ô số điện thoại
     * @return message lỗi hoặc {@code null}
     */
    public static String phoneError(String v) {
        if (v == null || v.isBlank()) return null;
        return v.trim().matches(PHONE_RE) ? null : "Số điện thoại không hợp lệ";
    }

    /**
     * Kiểm tra định dạng mã số thuế (10-14 chữ số, cho phép dấu gạch ngang).
     *
     * @param v giá trị ô mã số thuế
     * @return message lỗi hoặc {@code null}
     */
    public static String taxCodeError(String v) {
        if (v == null || v.isBlank()) return null;
        return v.trim().matches(TAX_CODE_RE) ? null : "Mã số thuế không hợp lệ (10–14 chữ số)";
    }
}
