package vn.com.be_crm.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi nhân viên kích hoạt tài khoản và đặt mật khẩu lần đầu.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivateAccountCommand {

    /** Token kích hoạt từ link email. */
    private String token;

    /** Mật khẩu mới do nhân viên đặt (tối thiểu 8 ký tự). */
    private String newPassword;
}
