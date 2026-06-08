package vn.com.be_crm.application.auth.dto;

import java.util.List;

/**
 * Kết quả trả về sau khi đăng nhập thành công.
 *
 * @param token    JWT access token
 * @param id       ID người dùng
 * @param email    email tài khoản
 * @param fullName họ tên đầy đủ
 * @param roles    danh sách code vai trò (vd: ADMIN, SALES)
 */
public record LoginResult(String token, Long id, String email, String fullName, List<String> roles) {
}
