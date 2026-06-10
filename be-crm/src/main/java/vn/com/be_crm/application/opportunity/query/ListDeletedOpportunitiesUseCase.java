package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

/** Use case lấy danh sách cơ hội đã xóa (thùng rác). */
public class ListDeletedOpportunitiesUseCase {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedOpportunitiesUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách cơ hội trong thùng rác (30 ngày gần nhất).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param req     tham số phân trang
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
