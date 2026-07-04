package vn.com.be_crm.application.campaign.query;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

/** Use case lấy danh sách chiến dịch đã xóa (thùng rác). */
public class ListDeletedCampaignsUseCase {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public ListDeletedCampaignsUseCase(ICampaignRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách chiến dịch trong thùng rác (30 ngày gần nhất).
     * @param userId ID người dùng hiện tại @param isAdmin true nếu admin @param req phân trang
     */
    public PageResult<DeletedItemResult> execute(Long userId, boolean isAdmin, PageRequest req) {
        return repo.findDeleted(userId, isAdmin, req);
    }
}
