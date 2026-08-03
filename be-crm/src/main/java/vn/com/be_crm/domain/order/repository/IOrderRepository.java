package vn.com.be_crm.domain.order.repository;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.entity.OrderItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho Order.
 */
public interface IOrderRepository {

    /** Lưu mới hoặc cập nhật đơn hàng. @param o @return entity sau khi lưu */
    Order save(Order o);

    /**
     * Tìm đơn hàng theo mã (code) chưa xóa mềm — dùng cho import UPDATE/BOTH.
     * @param code mã đơn hàng @return Optional
     */
    Optional<Order> findByCode(String code);

    /**
     * Lưu đơn hàng kèm danh sách dòng hàng trong một transaction.
     * @param o     domain entity đơn hàng
     * @param items danh sách dòng hàng (orderId sẽ được gán sau khi lưu đơn hàng)
     * @return đơn hàng sau khi lưu
     */
    Order saveWithItems(Order o, List<OrderItem> items);

    /** Tìm đơn hàng theo ID. @param id @return Optional */
    Optional<Order> findById(Long id);

    /**
     * Xóa mềm đơn hàng theo ID, ghi nhận người xóa.
     * @param id        ID cần xóa
     * @param deletedBy ID người thực hiện xóa
     */
    void deleteById(Long id, Long deletedBy);

    /** Lấy danh sách đơn hàng có phân trang. @param r @return PageResult */
    PageResult<Order> findAll(PageRequest r);

    /**
     * Lấy danh sách đơn hàng đã xóa mềm trong 30 ngày gần nhất.
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param r       tham số phân trang
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /** Khôi phục đơn hàng từ thùng rác. @param id */
    void restoreById(Long id);

    /** Ẩn đơn hàng khỏi thùng rác. @param id */
    void purgeById(Long id);

    /**
     * Bàn giao hàng loạt đơn hàng sang người dùng mới.
     * @param ids              danh sách ID cần bàn giao
     * @param toUserId         ID người nhận
     * @param currentUserId    ID người thực hiện
     * @param isAdminOrManager true → bàn giao bất kỳ; false → chỉ bàn giao bản ghi mình là owner
     */
    void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager);

    /**
     * Bàn giao toàn bộ đơn hàng của fromUserId sang toUserId.
     * @param fromUserId ID người bàn giao
     * @param toUserId   ID người nhận
     */
    void handoverAll(Long fromUserId, Long toUserId);
}
