package vn.com.be_crm.domain.activity.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.activity.entity.Activity;

import java.util.Optional;

/**
 * Port lưu trữ cho Activity.
 */
public interface IActivityRepository {

    /**
     * Lưu mới hoặc cập nhật hoạt động.
     *
     * @param activity entity cần lưu
     * @return entity sau khi lưu
     */
    Activity save(Activity activity);

    /**
     * Tìm hoạt động theo ID.
     *
     * @param id ID hoạt động
     * @return Optional chứa Activity nếu tìm thấy
     */
    Optional<Activity> findById(Long id);

    /**
     * Xóa hoạt động theo ID.
     *
     * @param id ID hoạt động cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách hoạt động có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách Activity
     */
    PageResult<Activity> findAll(PageRequest request);
}
