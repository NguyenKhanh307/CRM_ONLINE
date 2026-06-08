package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.AssignRolePermissionCommand;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IRolePermissionRepository;

/**
 * Use case thu hồi quyền hạn khỏi vai trò.
 */
public class RevokeRolePermissionUseCase implements IUseCase<AssignRolePermissionCommand, Void> {

    private final IRolePermissionRepository repository;

    /** @param repository port lưu trữ RolePermission */
    public RevokeRolePermissionUseCase(IRolePermissionRepository repository) {
        this.repository = repository;
    }

    /**
     * Xóa liên kết role-permission khỏi DB.
     *
     * @param command roleId và permissionId cần thu hồi
     * @return null
     */
    @Override
    public Void execute(AssignRolePermissionCommand command) {
        repository.deleteByRoleIdAndPermissionId(command.getRoleId(), command.getPermissionId());
        return null;
    }
}
