package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JSON input khi cập nhật vai trò.
 */
@Getter
@NoArgsConstructor
public class UpdateRoleRequest {

    @Size(max = 40)
    private String name;

    @Size(max = 50)
    private String description;
}
