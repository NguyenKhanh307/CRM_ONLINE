package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JSON input khi gán / thu hồi vai trò cho người dùng.
 */
@Getter
@NoArgsConstructor
public class AssignUserRoleRequest {

    @NotNull(message = "roleId không được để trống")
    private Long roleId;
}
