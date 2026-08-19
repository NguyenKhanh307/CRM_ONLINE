package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.CampaignCacRow;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * Chi phí trên mỗi lead/cơ hội/đơn hàng (CAC) theo từng chiến dịch trong kỳ.
 */
public class GetCampaignCacUseCase implements IUseCase<String, List<CampaignCacRow>> {

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetCampaignCacUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    /**
     * @param period mã kỳ (month|quarter|year)
     * @return danh sách CAC theo chiến dịch
     */
    @Override
    public List<CampaignCacRow> execute(String period) {
        return repo.campaignCac(PeriodRanges.current(period));
    }
}
