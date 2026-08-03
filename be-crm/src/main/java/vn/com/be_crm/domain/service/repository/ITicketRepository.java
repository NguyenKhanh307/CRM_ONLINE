package vn.com.be_crm.domain.service.repository;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho Ticket (phiếu hỗ trợ / yêu cầu sau bán).
 */
public interface ITicketRepository {

    /** Lưu mới hoặc cập nhật phiếu. @param t @return entity sau khi lưu */
    Ticket save(Ticket t);

    /**
     * Tìm phiếu theo mã (chưa xóa mềm).
     * @param code mã phiếu @return Optional
     */
    Optional<Ticket> findByCode(String code);

    /**
     * Lưu phiếu kèm danh sách dòng hàng trả/đổi trong một transaction.
     * @param t     domain entity phiếu
     * @param items danh sách dòng hàng (ticketId sẽ gán sau khi lưu phiếu)
     * @return phiếu sau khi lưu
     */
    Ticket saveWithReturnItems(Ticket t, List<TicketReturnItem> items);

    /** Tìm phiếu theo ID (chưa xóa mềm). @param id @return Optional */
    Optional<Ticket> findById(Long id);

    /**
     * Xóa mềm phiếu, ghi nhận người xóa.
     * @param id        ID cần xóa
     * @param deletedBy ID người thực hiện
     */
    void deleteById(Long id, Long deletedBy);

    /** Lấy danh sách phiếu có phân trang. @param r @return PageResult */
    PageResult<Ticket> findAll(PageRequest r);

    /**
     * Lấy danh sách phiếu đã xóa mềm trong 30 ngày gần nhất.
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param r       tham số phân trang
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /** Khôi phục phiếu từ thùng rác. @param id */
    void restoreById(Long id);

    /** Ẩn phiếu khỏi thùng rác. @param id */
    void purgeById(Long id);

    /**
     * Bàn giao hàng loạt phiếu sang nhân viên xử lý mới.
     * @param ids              danh sách ID
     * @param toUserId         ID người nhận
     * @param currentUserId    ID người thực hiện
     * @param isAdminOrManager true → bàn giao bất kỳ; false → chỉ phiếu mình đang xử lý
     */
    void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager);
}
