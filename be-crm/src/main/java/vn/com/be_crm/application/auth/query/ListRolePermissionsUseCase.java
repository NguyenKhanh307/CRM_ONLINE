package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.PermissionResult;
import vn.com.be_crm.application.auth.mapper.PermissionCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;
import vn.com.be_crm.domain.auth.repository.IRolePermissionRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case lấy danh sách quyền đã gán cho một vai trò.
 */
public class ListRolePermissionsUseCase implements IUseCase<Long, List<PermissionResult>> {

    private final IRolePermissionRepository rolePermissionRepository;
    private final IPermissionRepository permissionRepository;

    /**
     * @param rolePermissionRepository port lưu trữ RolePermission
     * @param permissionRepository     port lưu trữ Permission
     */
    public ListRolePermissionsUseCase(IRolePermissionRepository rolePermissionRepository,
                                       IPermissionRepository permissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Lấy danh sách PermissionResult của một vai trò theo roleId.
     *
     * @param roleId ID vai trò
     * @return danh sách PermissionResult đã gán cho vai trò
     */
    @Override
    public List<PermissionResult> execute(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId).stream()
                .map(rp -> permissionRepository.findById(rp.getPermissionId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(PermissionCommandMapper::toResult)
                .collect(Collectors.toList());
    }
}
