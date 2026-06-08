package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JSON input khi gán / thu hồi quyền cho vai trò.
 */
@Getter
@NoArgsConstructor
public class AssignRolePermissionRequest {

    @NotNull(message = "permissionId không được để trống")
    private Long permissionId;
}
