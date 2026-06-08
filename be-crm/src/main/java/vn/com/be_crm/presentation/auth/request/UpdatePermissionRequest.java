package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JSON input khi cập nhật quyền hạn.
 */
@Getter
@NoArgsConstructor
public class UpdatePermissionRequest {

    @Size(max = 50)
    private String name;

    @Size(max = 20)
    private String module;

    @Size(max = 50)
    private String description;
}
