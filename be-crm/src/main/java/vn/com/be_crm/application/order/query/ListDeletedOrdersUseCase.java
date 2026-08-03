package vn.com.be_crm.application.order.query;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

/** Use case lấy danh sách đơn hàng đã xóa (thùng rác). */
public class ListDeletedOrdersUseCase {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedOrdersUseCase(IOrderRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách đơn hàng trong thùng rác (30 ngày gần nhất).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param req     tham số phân trang
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
