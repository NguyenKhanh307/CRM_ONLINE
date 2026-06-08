package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi tạo mới vai trò.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleCommand {

    @NotBlank(message = "Mã vai trò không được để trống")
    @Size(max = 20, message = "Mã vai trò tối đa 20 ký tự")
    private String code;

    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 40, message = "Tên vai trò tối đa 40 ký tự")
    private String name;

    @Size(max = 50, message = "Mô tả tối đa 50 ký tự")
    private String description;

    private Boolean isSystem;
}
