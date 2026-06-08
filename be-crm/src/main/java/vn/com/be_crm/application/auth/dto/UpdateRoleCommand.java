package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi cập nhật vai trò.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleCommand {

    @NotNull(message = "ID không được để trống")
    private Long id;

    @Size(max = 40, message = "Tên vai trò tối đa 40 ký tự")
    private String name;

    @Size(max = 50, message = "Mô tả tối đa 50 ký tự")
    private String description;
}
