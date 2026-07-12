package vn.com.be_crm.infrastructure.shared.audit;

/**
 * Giữ ID người dùng của request hiện tại trong ThreadLocal, để tầng Hibernate
 * ({@link AuditInterceptor}, {@link AuditStamper}) đóng dấu {@code created_by}/{@code updated_by}
 * mà không phải luồn userId qua 68 use case.
 *
 * <p>Được set ở {@code JwtAuthFilter} (nơi duy nhất parse token) và <b>bắt buộc</b> clear trong
 * {@code finally} — Tomcat tái sử dụng thread từ pool, quên clear là danh tính người dùng rò rỉ
 * sang request kế tiếp.
 *
 * <p>Không có user (endpoint public như web tracking) → trả null → {@code created_by} để NULL,
 * đúng ngữ nghĩa "bản ghi do khách ẩn danh tạo".
 */
public final class CurrentUserHolder {

    private static final ThreadLocal<Long> CURRENT = new ThreadLocal<>();

    private CurrentUserHolder() {}

    /** @return ID người dùng của request hiện tại, hoặc null nếu request không có JWT */
    public static Long get() {
        return CURRENT.get();
    }

    /** @param userId ID người dùng lấy từ claim của JWT */
    public static void set(Long userId) {
        CURRENT.set(userId);
    }

    /** Xóa giá trị khỏi thread hiện tại — LUÔN gọi trong khối {@code finally}. */
    public static void clear() {
        CURRENT.remove();
    }
}
