package vn.com.be_crm.infrastructure.shared.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import vn.com.be_crm.application.shared.security.IPasswordEncoder;

/**
 * Implement IPasswordEncoder bằng BCrypt (Spring Security Crypto).
 */
@Component
public class BcryptPasswordEncoderImpl implements IPasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Kiểm tra mật khẩu thô khớp với hash BCrypt.
     *
     * @param rawPassword     mật khẩu người dùng nhập
     * @param encodedPassword hash BCrypt đã lưu trong DB
     * @return true nếu khớp
     */
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Mã hóa mật khẩu thô thành hash BCrypt.
     *
     * @param rawPassword mật khẩu người dùng nhập
     * @return mật khẩu đã hash
     */
    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
