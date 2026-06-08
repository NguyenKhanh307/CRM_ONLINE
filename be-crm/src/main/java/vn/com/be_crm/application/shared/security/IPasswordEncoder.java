package vn.com.be_crm.application.shared.security;

/**
 * Port kiểm tra mật khẩu — application layer không biết chi tiết thuật toán mã hóa.
 */
public interface IPasswordEncoder {

    /**
     * Kiểm tra mật khẩu thô khớp với mật khẩu đã mã hóa.
     *
     * @param rawPassword     mật khẩu người dùng nhập
     * @param encodedPassword mật khẩu đã lưu (đã hash)
     * @return true nếu khớp
     */
    boolean matches(String rawPassword, String encodedPassword);
}
