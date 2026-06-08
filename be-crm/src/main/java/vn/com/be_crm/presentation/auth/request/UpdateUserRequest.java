package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.auth.enums.UserStatus;

/**
 * JSON input khi cập nhật người dùng.
 */
@Getter
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(max = 30)
    private String fullName;

    @Size(max = 11)
    private String phone;

    private String avatarUrl;
    private Long unitId;
    private UserStatus status;
}
