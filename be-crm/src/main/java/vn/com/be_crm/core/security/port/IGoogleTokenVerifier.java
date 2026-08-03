package vn.com.be_crm.core.security.port;

/**
 * Port xác thực Google ID token — application layer không biết chi tiết cách gọi Google.
 */
public interface IGoogleTokenVerifier {

    /**
     * Xác thực ID token do Google Identity Services cấp và trích thông tin người dùng.
     *
     * @param idToken JWT ID token nhận từ FE
     * @return thông tin người dùng Google (email, tên, ảnh)
     * @throws vn.com.be_crm.core.error.frontend.DomainException nếu token không hợp lệ
     */
    GoogleUserInfo verify(String idToken);

    /**
     * Thông tin người dùng trích từ Google ID token.
     *
     * @param email         địa chỉ email Google
     * @param name          họ tên hiển thị
     * @param picture       URL ảnh đại diện Google (có thể null)
     * @param emailVerified email đã được Google xác minh hay chưa
     */
    record GoogleUserInfo(String email, String name, String picture, boolean emailVerified) {}
}
