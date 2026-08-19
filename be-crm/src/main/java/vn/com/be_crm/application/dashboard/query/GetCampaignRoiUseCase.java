package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.RankedItem;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * Top chiến dịch theo ROI trong kỳ — phục vụ trang phân tích so sánh (/phan-tich).
 */
public class GetCampaignRoiUseCase implements IUseCase<String, List<RankedItem>> {

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetCampaignRoiUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    /**
     * @param period mã kỳ (month|quarter|year)
     * @return top 8 chiến dịch theo ROI
     */
    @Override
    public List<RankedItem> execute(String period) {
        return repo.campaignRoi(PeriodRanges.current(period));
    }
}
