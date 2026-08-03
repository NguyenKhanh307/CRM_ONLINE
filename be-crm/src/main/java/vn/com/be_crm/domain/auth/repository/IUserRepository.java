package vn.com.be_crm.domain.auth.repository;

import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.auth.entity.User;

import java.util.Optional;

/**
 * Port lưu trữ cho User — chỉ domain interface, không biết Hibernate.
 */
public interface IUserRepository {

    /**
     * Lưu mới hoặc cập nhật người dùng.
     *
     * @param user entity cần lưu
     * @return entity sau khi lưu
     */
    User save(User user);

    /**
     * Tìm người dùng theo ID (bỏ qua bản ghi đã xóa mềm).
     *
     * @param id ID người dùng
     * @return Optional chứa User nếu tìm thấy và chưa bị xóa
     */
    Optional<User> findById(Long id);

    /**
     * Xóa mềm người dùng — set deleted_at = now().
     *
     * @param id ID người dùng cần xóa mềm
     */
    void deleteById(Long id);

    /**
     * Tìm người dùng theo email (bỏ qua bản ghi đã xóa mềm).
     *
     * @param email địa chỉ email cần tìm
     * @return Optional chứa User nếu tìm thấy và chưa bị xóa
     */
    Optional<User> findByEmail(String email);

    /**
     * Tìm người dùng theo activation token, chỉ trả về nếu chưa bị xóa mềm.
     *
     * @param token activation token cần tra cứu
     * @return Optional chứa User nếu tìm thấy
     */
    Optional<User> findByActivationToken(String token);

    /**
     * Lấy danh sách người dùng chưa xóa, có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách User
     */
    PageResult<User> findAll(PageRequest request);
}
