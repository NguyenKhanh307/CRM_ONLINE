package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.AdminDashboardResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

/**
 * Use case lấy dữ liệu Dashboard cho vai trò ADMIN theo kỳ thời gian.
 */
public class GetAdminDashboardUseCase implements IUseCase<String, AdminDashboardResult> {
    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetAdminDashboardUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    /**
     * Lấy dashboard admin.
     *
     * @param period mã kỳ ("month" | "quarter" | "year")
     * @return dữ liệu dashboard admin
     */
    @Override
    public AdminDashboardResult execute(String period) {
        return repo.getAdmin(PeriodRanges.current(period), PeriodRanges.previous(period), PeriodRanges.seriesFrom());
    }
}
