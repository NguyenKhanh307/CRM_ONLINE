package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.GroupedStatusRow;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * Lý do thua theo từng nhân viên trong kỳ — toàn đội (nhiều nhóm) hoặc cá nhân (1 nhóm) tùy ownerId.
 */
public class GetLossReasonsByOwnerUseCase implements IUseCase<GetLossReasonsByOwnerUseCase.Query, List<GroupedStatusRow>> {

    /**
     * @param ownerId null = toàn đội (ADMIN/manager); khác null = lọc theo owner (nhân viên)
     * @param period  mã kỳ (month|quarter|year)
     */
    public record Query(Long ownerId, String period) {
    }

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetLossReasonsByOwnerUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<GroupedStatusRow> execute(Query q) {
        return repo.lossReasonsByOwner(q.ownerId(), PeriodRanges.current(q.period()));
    }
}
