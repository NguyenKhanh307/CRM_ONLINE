package vn.com.be_crm.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Output DTO tối giản cho một liên kết user-role — dùng để FE biết người dùng nào đã thuộc một nhóm.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResult {
    private Long userId;
    private Long roleId;
}
