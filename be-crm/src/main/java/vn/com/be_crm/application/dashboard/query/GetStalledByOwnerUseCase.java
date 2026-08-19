package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.RankedItem;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * Số cơ hội "treo" theo từng nhân viên (top 8) — chỉ ADMIN/SALES_MANAGER xem.
 */
public class GetStalledByOwnerUseCase implements IUseCase<Integer, List<RankedItem>> {

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetStalledByOwnerUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    /**
     * @param days ngưỡng số ngày không hoạt động; null = dùng mặc định
     * @return danh sách xếp hạng theo nhân viên
     */
    @Override
    public List<RankedItem> execute(Integer days) {
        return repo.stalledByOwner(days != null ? days : DashboardDefaults.STALLED_DAYS_DEFAULT);
    }
}
