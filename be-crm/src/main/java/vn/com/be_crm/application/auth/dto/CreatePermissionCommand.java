package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi tạo mới quyền hạn.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermissionCommand {

    @NotBlank(message = "Mã quyền không được để trống")
    @Size(max = 30, message = "Mã quyền tối đa 30 ký tự")
    private String code;

    @NotBlank(message = "Tên quyền không được để trống")
    @Size(max = 50, message = "Tên quyền tối đa 50 ký tự")
    private String name;

    @Size(max = 20, message = "Module tối đa 20 ký tự")
    private String module;

    @Size(max = 50, message = "Mô tả tối đa 50 ký tự")
    private String description;
}
