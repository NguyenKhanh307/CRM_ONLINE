package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.SalesDashboardResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

/**
 * Use case lấy dữ liệu Dashboard kinh doanh cho SALES_MANAGER (toàn đội) hoặc SALES_STAFF (cá nhân).
 */
public class GetSalesDashboardUseCase implements IUseCase<SalesDashboardQuery, SalesDashboardResult> {
    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetSalesDashboardUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    /**
     * Lấy dashboard kinh doanh theo tham số.
     *
     * @param q tham số (owner, includeTeam, period)
     * @return dữ liệu dashboard kinh doanh
     */
    @Override
    public SalesDashboardResult execute(SalesDashboardQuery q) {
        return repo.getSales(q.ownerId(), q.includeTeam(),
                PeriodRanges.current(q.period()), PeriodRanges.previous(q.period()), PeriodRanges.seriesFrom());
    }
}
