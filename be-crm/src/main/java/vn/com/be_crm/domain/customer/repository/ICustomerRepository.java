package vn.com.be_crm.domain.customer.repository;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.customer.entity.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho Customer.
 */
public interface ICustomerRepository {

    /**
     * Lưu mới hoặc cập nhật khách hàng.
     * @param customer domain entity @return entity sau khi lưu
     */
    Customer save(Customer customer);

    /**
     * Tìm khách hàng theo ID (chưa xóa mềm).
     * @param id ID @return Optional
     */
    Optional<Customer> findById(Long id);

    /**
     * Xóa mềm khách hàng theo ID, ghi nhận người xóa.
     * @param id        ID cần xóa
     * @param deletedBy ID người thực hiện xóa
     */
    void deleteById(Long id, Long deletedBy);

    /**
     * Lấy danh sách khách hàng chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<Customer> findAll(PageRequest r);

    /**
     * Lấy danh sách khách hàng đã xóa mềm trong 30 ngày gần nhất (dùng cho thùng rác).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin (xem tất cả), false nếu chỉ xem của mình
     * @param r       tham số phân trang
     * @return PageResult<DeletedItemResult>
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /**
     * Khôi phục khách hàng từ thùng rác.
     * @param id ID cần khôi phục
     */
    void restoreById(Long id);

    /**
     * Ẩn khách hàng khỏi thùng rác (set is_purged = true).
     * @param id ID cần ẩn
     */
    void purgeById(Long id);

    /**
     * Bàn giao hàng loạt khách hàng sang người dùng mới.
     * @param ids              danh sách ID cần bàn giao
     * @param toUserId         ID người nhận
     * @param currentUserId    ID người thực hiện
     * @param isAdminOrManager true → bàn giao bất kỳ; false → chỉ bàn giao bản ghi mình là owner
     */
    void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager);

    /**
     * Bàn giao toàn bộ khách hàng của fromUserId sang toUserId.
     * @param fromUserId ID người bàn giao
     * @param toUserId   ID người nhận
     */
    void handoverAll(Long fromUserId, Long toUserId);
}
