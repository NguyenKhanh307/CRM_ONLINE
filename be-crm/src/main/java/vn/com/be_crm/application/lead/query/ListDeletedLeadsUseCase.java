package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

/** Use case lấy danh sách tiềm năng đã xóa (thùng rác). */
public class ListDeletedLeadsUseCase {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedLeadsUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách tiềm năng trong thùng rác (30 ngày gần nhất).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin (xem tất cả)
     * @param req     tham số phân trang
     * @return PageResult<DeletedItemResult>
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
