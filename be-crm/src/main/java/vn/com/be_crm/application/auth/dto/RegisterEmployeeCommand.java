package vn.com.be_crm.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi admin đăng ký tài khoản nhân viên mới.
 * Không có passwordHash — nhân viên tự đặt mật khẩu qua link kích hoạt.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterEmployeeCommand {

    /** Email nhân viên — phải là @gmail.com. */
    private String email;

    /** Họ và tên đầy đủ. */
    private String fullName;

    /** Số điện thoại (tùy chọn). */
    private String phone;

    /** ID vai trò gán ngay khi đăng ký. */
    private Long roleId;

    /** URL gốc frontend để tạo activation link (vd: http://localhost:5173). */
    private String frontendBaseUrl;
}
