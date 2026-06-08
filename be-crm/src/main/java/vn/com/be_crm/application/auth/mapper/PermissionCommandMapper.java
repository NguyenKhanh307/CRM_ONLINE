package vn.com.be_crm.application.auth.mapper;

import vn.com.be_crm.application.auth.dto.CreatePermissionCommand;
import vn.com.be_crm.application.auth.dto.PermissionResult;
import vn.com.be_crm.application.auth.dto.UpdatePermissionCommand;
import vn.com.be_crm.domain.auth.entity.Permission;

/**
 * Chuyển đổi giữa Command ↔ Permission domain entity ↔ PermissionResult.
 */
public class PermissionCommandMapper {

    /**
     * Tạo Permission domain entity từ CreatePermissionCommand.
     *
     * @param cmd command tạo mới
     * @return Permission domain entity chưa có ID
     */
    public static Permission toEntity(CreatePermissionCommand cmd) {
        return Permission.builder()
                .code(cmd.getCode())
                .name(cmd.getName())
                .module(cmd.getModule())
                .description(cmd.getDescription())
                .build();
    }

    /**
     * Cập nhật Permission domain entity từ UpdatePermissionCommand.
     *
     * @param cmd      command cập nhật
     * @param existing entity hiện tại
     * @return Permission đã cập nhật
     */
    public static Permission toEntity(UpdatePermissionCommand cmd, Permission existing) {
        return Permission.builder()
                .id(existing.getId())
                .code(existing.getCode())
                .name(cmd.getName() != null ? cmd.getName() : existing.getName())
                .module(cmd.getModule() != null ? cmd.getModule() : existing.getModule())
                .description(cmd.getDescription() != null ? cmd.getDescription() : existing.getDescription())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    /**
     * Chuyển Permission domain entity sang PermissionResult.
     *
     * @param entity domain entity
     * @return PermissionResult
     */
    public static PermissionResult toResult(Permission entity) {
        return PermissionResult.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .module(entity.getModule())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PermissionCommandMapper() {}
}
