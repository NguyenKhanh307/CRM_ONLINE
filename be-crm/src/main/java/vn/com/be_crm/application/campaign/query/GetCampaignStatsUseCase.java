package vn.com.be_crm.application.campaign.query;

import vn.com.be_crm.application.campaign.dto.CampaignStatsResult;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

/** Use case tính chỉ số hiệu quả (ROI) của chiến dịch. */
public class GetCampaignStatsUseCase implements IUseCase<Long, CampaignStatsResult> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public GetCampaignStatsUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Lấy thống kê ROI theo campaignId. @param campaignId @return CampaignStatsResult */
    @Override public CampaignStatsResult execute(Long campaignId) {
        return repo.getStats(campaignId);
    }
}
