package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JSON input khi tạo mới quyền hạn.
 */
@Getter
@NoArgsConstructor
public class CreatePermissionRequest {

    @NotBlank(message = "Mã quyền không được để trống")
    @Size(max = 30)
    private String code;

    @NotBlank(message = "Tên quyền không được để trống")
    @Size(max = 50)
    private String name;

    @Size(max = 20)
    private String module;

    @Size(max = 50)
    private String description;
}
