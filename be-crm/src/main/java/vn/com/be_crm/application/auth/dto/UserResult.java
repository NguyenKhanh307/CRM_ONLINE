package vn.com.be_crm.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.auth.enums.UserStatus;

import java.time.LocalDateTime;

/**
 * Output DTO của User UseCase — không expose password hash.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResult {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private UserStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer dataAccessFromYear;
}
