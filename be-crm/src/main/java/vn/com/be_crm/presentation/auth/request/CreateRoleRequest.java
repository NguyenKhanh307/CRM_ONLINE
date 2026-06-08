package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JSON input khi tạo mới vai trò.
 */
@Getter
@NoArgsConstructor
public class CreateRoleRequest {

    @NotBlank(message = "Mã vai trò không được để trống")
    @Size(max = 20)
    private String code;

    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 40)
    private String name;

    @Size(max = 50)
    private String description;

    private Boolean isSystem;
}
