package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * HTTP request body cho POST /api/auth/change-password.
 */
@Getter
@NoArgsConstructor
public class ChangePasswordRequest {

    /** Mật khẩu hiện tại để xác minh. */
    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String currentPassword;

    /** Mật khẩu mới (tối thiểu 8 ký tự). */
    @NotBlank
    @Size(min = 8, message = "Mật khẩu mới phải có ít nhất 8 ký tự")
    private String newPassword;
}
