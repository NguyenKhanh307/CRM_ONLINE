package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.EmployeeWinRateRow;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * Bảng xếp hạng nhân viên theo tỉ lệ thắng (kèm doanh thu) trong kỳ — chỉ ADMIN/SALES_MANAGER xem.
 */
public class GetWinRateLeaderboardUseCase implements IUseCase<String, List<EmployeeWinRateRow>> {

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetWinRateLeaderboardUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<EmployeeWinRateRow> execute(String period) {
        return repo.winRateLeaderboard(PeriodRanges.current(period));
    }
}
