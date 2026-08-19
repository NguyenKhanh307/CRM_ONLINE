package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.TimeSeriesPoint;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * Tỉ lệ thắng-thua theo thời gian (12 tháng gần nhất) — toàn đội hoặc cá nhân tùy ownerId.
 */
public class GetWinRateTrendUseCase implements IUseCase<GetWinRateTrendUseCase.Query, List<TimeSeriesPoint>> {

    /**
     * @param ownerId null = toàn đội (ADMIN/manager); khác null = lọc theo owner (nhân viên)
     * @param period  mã kỳ, chỉ dùng để giữ đồng bộ tham số với các use case khác (không ảnh hưởng chuỗi 12 tháng)
     */
    public record Query(Long ownerId, String period) {
    }

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetWinRateTrendUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<TimeSeriesPoint> execute(Query q) {
        return repo.winRateTrend(q.ownerId(), PeriodRanges.seriesFrom());
    }
}
