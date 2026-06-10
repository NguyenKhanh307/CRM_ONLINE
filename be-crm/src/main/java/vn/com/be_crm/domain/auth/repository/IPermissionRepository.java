package vn.com.be_crm.domain.auth.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.auth.entity.Permission;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho Permission.
 */
public interface IPermissionRepository {

    /**
     * Lưu mới hoặc cập nhật quyền hạn.
     *
     * @param permission entity cần lưu
     * @return entity sau khi lưu
     */
    Permission save(Permission permission);

    /**
     * Tìm quyền theo ID.
     *
     * @param id ID quyền
     * @return Optional chứa Permission nếu tìm thấy
     */
    Optional<Permission> findById(Long id);

    /**
     * Xóa quyền theo ID.
     *
     * @param id ID quyền cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách quyền có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách Permission
     */
    PageResult<Permission> findAll(PageRequest request);

    /**
     * Lấy tất cả permission code của user thông qua user_roles → role_permissions.
     *
     * @param userId ID người dùng
     * @return danh sách code (vd: "lead.view", "order.create")
     */
    List<String> findCodesByUserId(Long userId);
}
