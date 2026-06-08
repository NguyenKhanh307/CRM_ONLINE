package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.auth.enums.UserStatus;

/**
 * Input DTO khi tạo mới người dùng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 50, message = "Email tối đa 50 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String passwordHash;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 30, message = "Họ tên tối đa 30 ký tự")
    private String fullName;

    @Size(max = 11, message = "Số điện thoại tối đa 11 ký tự")
    private String phone;

    private String avatarUrl;
    private Long unitId;
    private UserStatus status;
}
