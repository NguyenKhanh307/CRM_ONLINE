package vn.com.be_crm.domain.auth.repository;

import vn.com.be_crm.domain.auth.entity.RolePermission;

import java.util.List;

/**
 * Port lưu trữ cho RolePermission (N:N roles - permissions).
 */
public interface IRolePermissionRepository {

    /**
     * Gán quyền cho vai trò.
     *
     * @param rolePermission entity gán quyền
     * @return entity sau khi lưu
     */
    RolePermission save(RolePermission rolePermission);

    /**
     * Thu hồi quyền khỏi vai trò.
     *
     * @param roleId       ID vai trò
     * @param permissionId ID quyền cần thu hồi
     */
    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * Lấy tất cả quyền của một vai trò.
     *
     * @param roleId ID vai trò
     * @return danh sách RolePermission
     */
    List<RolePermission> findByRoleId(Long roleId);
}
