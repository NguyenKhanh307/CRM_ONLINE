package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.CampaignRevenueComparisonRow;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * So sánh doanh thu kỳ này/kỳ trước cho các chiến dịch đang chạy song song.
 */
public class GetCampaignPeriodComparisonUseCase implements IUseCase<String, List<CampaignRevenueComparisonRow>> {

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetCampaignPeriodComparisonUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<CampaignRevenueComparisonRow> execute(String period) {
        return repo.campaignPeriodComparison(PeriodRanges.current(period), PeriodRanges.previous(period));
    }
}
