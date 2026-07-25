package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.auth.enums.UserStatus;

/**
 * Input DTO khi cập nhật người dùng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCommand {

    @NotNull(message = "ID không được để trống")
    private Long id;

    @Size(max = 30, message = "Họ tên tối đa 30 ký tự")
    private String fullName;

    @Size(max = 11, message = "Số điện thoại tối đa 11 ký tự")
    private String phone;

    private String avatarUrl;
    private UserStatus status;
    private Integer dataAccessFromYear;
}
