package vn.com.be_crm.application.auth.command;

/**
 * Input DTO đăng nhập bằng Google — chỉ chứa ID token do FE lấy từ Google Identity Services.
 *
 * @param idToken JWT ID token của Google
 */
public record GoogleLoginCommand(String idToken) {}
