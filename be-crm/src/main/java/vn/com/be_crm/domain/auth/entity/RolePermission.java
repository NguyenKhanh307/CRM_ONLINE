package vn.com.be_crm.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho liên kết N:N giữa vai trò và quyền hạn.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    /** ID bản ghi. */
    private Long id;

    /** ID vai trò. */
    private Long roleId;

    /** ID quyền hạn. */
    private Long permissionId;

    /** Thời điểm gán quyền. */
    private LocalDateTime createdAt;
}
