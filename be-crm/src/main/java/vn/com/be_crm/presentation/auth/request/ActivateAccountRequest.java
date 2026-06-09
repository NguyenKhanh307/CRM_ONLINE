package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * HTTP request body cho POST /api/auth/activate.
 */
@Getter
@NoArgsConstructor
public class ActivateAccountRequest {

    /** Token kích hoạt từ link email. */
    @NotBlank
    private String token;

    /** Mật khẩu mới (tối thiểu 8 ký tự). */
    @NotBlank
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String newPassword;
}
