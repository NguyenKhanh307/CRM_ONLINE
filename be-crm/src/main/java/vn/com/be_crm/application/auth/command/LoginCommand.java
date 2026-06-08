package vn.com.be_crm.application.auth.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Command đăng nhập — chứa email và mật khẩu thô.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginCommand {

    /** Email tài khoản. */
    @NotBlank
    @Email
    private String email;

    /** Mật khẩu thô (chưa hash). */
    @NotBlank
    private String password;
}
