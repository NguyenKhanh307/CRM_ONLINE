package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi gán quyền cho vai trò.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignRolePermissionCommand {

    @NotNull(message = "roleId không được để trống")
    private Long roleId;

    @NotNull(message = "permissionId không được để trống")
    private Long permissionId;
}
