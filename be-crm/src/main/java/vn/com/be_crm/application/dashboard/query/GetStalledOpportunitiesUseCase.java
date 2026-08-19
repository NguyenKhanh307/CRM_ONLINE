package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.CountedRankedList;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

/**
 * Cơ hội "treo" — đang mở, không có hoạt động chăm sóc trong N ngày.
 */
public class GetStalledOpportunitiesUseCase implements IUseCase<GetStalledOpportunitiesUseCase.Query, CountedRankedList> {

    /**
     * @param ownerId null = toàn đội (ADMIN/manager); khác null = lọc theo owner (nhân viên)
     * @param days    ngưỡng số ngày không hoạt động; null = dùng mặc định
     */
    public record Query(Long ownerId, Integer days) {
    }

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetStalledOpportunitiesUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    @Override
    public CountedRankedList execute(Query q) {
        int days = q.days() != null ? q.days() : DashboardDefaults.STALLED_DAYS_DEFAULT;
        return repo.stalledOpportunities(q.ownerId(), days);
    }
}
