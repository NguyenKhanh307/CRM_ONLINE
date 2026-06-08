package vn.com.be_crm.application.auth.mapper;

import vn.com.be_crm.application.auth.dto.CreateUserCommand;
import vn.com.be_crm.application.auth.dto.UpdateUserCommand;
import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.enums.UserStatus;

/**
 * Chuyển đổi giữa Command ↔ User domain entity ↔ UserResult.
 */
public class UserCommandMapper {

    /**
     * Tạo User domain entity từ CreateUserCommand.
     *
     * @param cmd command tạo mới
     * @return User domain entity chưa có ID
     */
    public static User toEntity(CreateUserCommand cmd) {
        return User.builder()
                .email(cmd.getEmail())
                .passwordHash(cmd.getPasswordHash())
                .fullName(cmd.getFullName())
                .phone(cmd.getPhone())
                .avatarUrl(cmd.getAvatarUrl())
                .unitId(cmd.getUnitId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : UserStatus.active)
                .build();
    }

    /**
     * Cập nhật User domain entity từ UpdateUserCommand.
     *
     * @param cmd      command cập nhật
     * @param existing entity hiện tại
     * @return User đã cập nhật
     */
    public static User toEntity(UpdateUserCommand cmd, User existing) {
        return User.builder()
                .id(existing.getId())
                .email(existing.getEmail())
                .passwordHash(existing.getPasswordHash())
                .fullName(cmd.getFullName() != null ? cmd.getFullName() : existing.getFullName())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : existing.getPhone())
                .avatarUrl(cmd.getAvatarUrl() != null ? cmd.getAvatarUrl() : existing.getAvatarUrl())
                .unitId(cmd.getUnitId() != null ? cmd.getUnitId() : existing.getUnitId())
                .status(cmd.getStatus() != null ? cmd.getStatus() : existing.getStatus())
                .lastLoginAt(existing.getLastLoginAt())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    /**
     * Chuyển User domain entity sang UserResult — bỏ password hash.
     *
     * @param entity domain entity
     * @return UserResult
     */
    public static UserResult toResult(User entity) {
        return UserResult.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .phone(entity.getPhone())
                .avatarUrl(entity.getAvatarUrl())
                .unitId(entity.getUnitId())
                .status(entity.getStatus())
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private UserCommandMapper() {}
}
