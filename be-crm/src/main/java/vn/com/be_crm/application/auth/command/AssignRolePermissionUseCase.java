package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.AssignRolePermissionCommand;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.RolePermission;
import vn.com.be_crm.domain.auth.repository.IRolePermissionRepository;

/**
 * Use case gán quyền hạn cho vai trò.
 */
public class AssignRolePermissionUseCase implements IUseCase<AssignRolePermissionCommand, Void> {

    private final IRolePermissionRepository repository;

    /** @param repository port lưu trữ RolePermission */
    public AssignRolePermissionUseCase(IRolePermissionRepository repository) {
        this.repository = repository;
    }

    /**
     * Tạo liên kết role-permission và lưu vào DB.
     *
     * @param command roleId và permissionId cần gán
     * @return null
     */
    @Override
    public Void execute(AssignRolePermissionCommand command) {
        RolePermission rp = RolePermission.builder()
                .roleId(command.getRoleId())
                .permissionId(command.getPermissionId())
                .build();
        repository.save(rp);
        return null;
    }
}
