package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.CountedRankedList;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

/**
 * Lead đang ở pool chung, chưa ai nhận chăm sóc — chỉ ADMIN/SALES_MANAGER xem.
 */
public class GetLeadPoolUseCase implements IUseCase<Void, CountedRankedList> {

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetLeadPoolUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    @Override
    public CountedRankedList execute(Void input) {
        return repo.leadPool();
    }
}
