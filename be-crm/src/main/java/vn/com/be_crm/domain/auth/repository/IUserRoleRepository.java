package vn.com.be_crm.domain.auth.repository;

import vn.com.be_crm.domain.auth.entity.UserRole;

import java.util.List;

/**
 * Port lưu trữ cho UserRole (N:N users - roles).
 */
public interface IUserRoleRepository {

    /**
     * Gán vai trò cho người dùng.
     *
     * @param userRole entity gán vai trò
     * @return entity sau khi lưu
     */
    UserRole save(UserRole userRole);

    /**
     * Thu hồi vai trò khỏi người dùng.
     *
     * @param userId ID người dùng
     * @param roleId ID vai trò cần thu hồi
     */
    void deleteByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * Lấy tất cả vai trò của một người dùng.
     *
     * @param userId ID người dùng
     * @return danh sách UserRole
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * Lấy danh sách code vai trò của một người dùng.
     *
     * @param userId ID người dùng
     * @return danh sách code vai trò (vd: ADMIN, SALES)
     */
    List<String> findRoleCodesByUserId(Long userId);

    /**
     * Lấy tất cả UserRole theo roleId (dùng để liệt kê thành viên trong nhóm).
     *
     * @param roleId ID vai trò
     * @return danh sách UserRole
     */
    List<UserRole> findByRoleId(Long roleId);
}
