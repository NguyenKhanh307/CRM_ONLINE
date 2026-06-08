package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.auth.enums.UserStatus;

/**
 * JSON input khi tạo mới người dùng.
 */
@Getter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Email không được để trống")
    @Email
    @Size(max = 50)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String passwordHash;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 30)
    private String fullName;

    @Size(max = 11)
    private String phone;

    private String avatarUrl;
    private Long unitId;
    private UserStatus status;
}
