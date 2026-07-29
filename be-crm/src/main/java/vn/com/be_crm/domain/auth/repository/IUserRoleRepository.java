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
     * Xóa toàn bộ vai trò hiện có của một người dùng — dùng để đảm bảo mỗi người chỉ thuộc một nhóm
     * trước khi gán vai trò mới.
     *
     * @param userId ID người dùng
     */
    void deleteByUserId(Long userId);

    /**
     * Lấy toàn bộ liên kết user-role hiện có (bảng nhỏ, tải hết một lần).
     * Dùng để FE biết người dùng nào đã thuộc một nhóm bất kỳ.
     *
     * @return danh sách UserRole
     */
    List<UserRole> findAll();

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

    /**
     * Lấy danh sách ID người dùng có ít nhất một vai trò trong tập code cho trước.
     * Dùng để xác định người nhận thông báo (vd ADMIN, SALES_MANAGER).
     *
     * @param roleCodes danh sách code vai trò
     * @return danh sách userId không trùng
     */
    List<Long> findUserIdsByRoleCodes(List<String> roleCodes);
}
