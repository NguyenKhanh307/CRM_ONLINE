package vn.com.be_crm.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi người dùng đang đăng nhập tự đổi mật khẩu của mình.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordCommand {

    /** ID người dùng (lấy từ JWT, không nhận từ body). */
    private Long userId;

    /** Mật khẩu hiện tại — dùng để xác minh trước khi đổi. */
    private String currentPassword;

    /** Mật khẩu mới (tối thiểu 8 ký tự). */
    private String newPassword;
}
