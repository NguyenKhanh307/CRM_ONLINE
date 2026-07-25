package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * HTTP request body cho POST /api/auth/register-employee.
 */
@Getter
@NoArgsConstructor
public class RegisterEmployeeRequest {

    /** Email nhân viên — phải là @gmail.com. */
    @NotBlank
    @Email
    @Pattern(regexp = ".*@gmail\\.com$", message = "Chỉ chấp nhận địa chỉ @gmail.com")
    @Size(max = 50)
    private String email;

    /** Họ và tên đầy đủ. */
    @NotBlank
    @Size(max = 30)
    private String fullName;

    /** Số điện thoại (tùy chọn). */
    @Size(max = 11)
    private String phone;

    /** ID vai trò gán ngay khi đăng ký. */
    private Long roleId;
}
