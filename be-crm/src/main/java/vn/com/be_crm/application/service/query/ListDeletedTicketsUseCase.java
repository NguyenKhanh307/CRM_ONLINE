package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

/** Use case lấy danh sách phiếu đã xóa (thùng rác). */
public class ListDeletedTicketsUseCase {
    private final ITicketRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedTicketsUseCase(ITicketRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách phiếu trong thùng rác (30 ngày gần nhất).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param req     tham số phân trang
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
