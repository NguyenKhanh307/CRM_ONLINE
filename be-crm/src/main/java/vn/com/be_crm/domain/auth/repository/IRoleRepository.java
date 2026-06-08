package vn.com.be_crm.domain.auth.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.auth.entity.Role;

import java.util.Optional;

/**
 * Port lưu trữ cho Role.
 */
public interface IRoleRepository {

    /**
     * Lưu mới hoặc cập nhật vai trò.
     *
     * @param role entity cần lưu
     * @return entity sau khi lưu
     */
    Role save(Role role);

    /**
     * Tìm vai trò theo ID.
     *
     * @param id ID vai trò
     * @return Optional chứa Role nếu tìm thấy
     */
    Optional<Role> findById(Long id);

    /**
     * Xóa vai trò theo ID (không cho xóa vai trò hệ thống — kiểm tra ở UseCase).
     *
     * @param id ID vai trò cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách vai trò có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách Role
     */
    PageResult<Role> findAll(PageRequest request);
}
