package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.KpiMetric;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

/**
 * % doanh thu quy kết được về marketing (có campaign_id) trên tổng doanh thu, kỳ này vs kỳ trước.
 */
public class GetCampaignAttributedRevenueUseCase implements IUseCase<String, KpiMetric> {

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetCampaignAttributedRevenueUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    @Override
    public KpiMetric execute(String period) {
        return repo.campaignAttributedRevenuePct(PeriodRanges.current(period), PeriodRanges.previous(period));
    }
}
