package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.DonutSegment;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * Tỉ lệ cơ hội có nguồn từ chiến dịch vs tự phát sinh trong kỳ.
 */
public class GetOpportunitySourceUseCase implements IUseCase<String, List<DonutSegment>> {

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetOpportunitySourceUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<DonutSegment> execute(String period) {
        return repo.opportunitySource(PeriodRanges.current(period));
    }
}
