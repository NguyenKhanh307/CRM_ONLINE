package vn.com.be_crm.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho liên kết N:N giữa người dùng và vai trò.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    /** ID bản ghi. */
    private Long id;

    /** ID người dùng. */
    private Long userId;

    /** ID vai trò. */
    private Long roleId;

    /** Thời điểm gán vai trò. */
    private LocalDateTime createdAt;
}
