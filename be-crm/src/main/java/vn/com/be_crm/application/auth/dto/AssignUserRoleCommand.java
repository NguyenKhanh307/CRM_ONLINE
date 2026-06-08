package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi gán vai trò cho người dùng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUserRoleCommand {

    @NotNull(message = "userId không được để trống")
    private Long userId;

    @NotNull(message = "roleId không được để trống")
    private Long roleId;
}
