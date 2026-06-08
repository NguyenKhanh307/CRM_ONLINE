package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi cập nhật quyền hạn.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionCommand {

    @NotNull(message = "ID không được để trống")
    private Long id;

    @Size(max = 50, message = "Tên quyền tối đa 50 ký tự")
    private String name;

    @Size(max = 20, message = "Module tối đa 20 ký tự")
    private String module;

    @Size(max = 50, message = "Mô tả tối đa 50 ký tự")
    private String description;
}
