package vn.com.be_crm.application.auth.mapper;

import vn.com.be_crm.application.auth.dto.CreateRoleCommand;
import vn.com.be_crm.application.auth.dto.RoleResult;
import vn.com.be_crm.application.auth.dto.UpdateRoleCommand;
import vn.com.be_crm.domain.auth.entity.Role;

/**
 * Chuyển đổi giữa Command ↔ Role domain entity ↔ RoleResult.
 */
public class RoleCommandMapper {

    /**
     * Tạo Role domain entity từ CreateRoleCommand.
     *
     * @param cmd command tạo mới
     * @return Role domain entity chưa có ID
     */
    public static Role toEntity(CreateRoleCommand cmd) {
        return Role.builder()
                .code(cmd.getCode())
                .name(cmd.getName())
                .description(cmd.getDescription())
                .isSystem(cmd.getIsSystem() != null ? cmd.getIsSystem() : false)
                .build();
    }

    /**
     * Cập nhật Role domain entity từ UpdateRoleCommand.
     *
     * @param cmd      command cập nhật
     * @param existing entity hiện tại
     * @return Role đã cập nhật
     */
    public static Role toEntity(UpdateRoleCommand cmd, Role existing) {
        return Role.builder()
                .id(existing.getId())
                .code(existing.getCode())
                .name(cmd.getName() != null ? cmd.getName() : existing.getName())
                .description(cmd.getDescription() != null ? cmd.getDescription() : existing.getDescription())
                .isSystem(existing.getIsSystem())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    /**
     * Chuyển Role domain entity sang RoleResult.
     *
     * @param entity domain entity
     * @return RoleResult
     */
    public static RoleResult toResult(Role entity) {
        return RoleResult.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .isSystem(entity.getIsSystem())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private RoleCommandMapper() {}
}
