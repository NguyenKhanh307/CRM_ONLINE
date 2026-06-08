package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * HTTP request body cho POST /api/auth/login.
 */
@Getter
@NoArgsConstructor
public class LoginRequest {

    /** Email tài khoản. */
    @NotBlank
    @Email
    private String email;

    /** Mật khẩu thô. */
    @NotBlank
    private String password;
}
