package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.application.dashboard.dto.RankedItem;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.util.List;

/**
 * Doanh thu theo chiến dịch trong kỳ — phục vụ trang phân tích so sánh (/phan-tich).
 */
public class GetRevenueByCampaignUseCase implements IUseCase<GetRevenueByCampaignUseCase.Query, List<RankedItem>> {

    /**
     * Tham số truy vấn.
     *
     * @param ownerId null = toàn bộ (ADMIN/manager); khác null = lọc theo owner (nhân viên)
     * @param period  mã kỳ (month|quarter|year)
     */
    public record Query(Long ownerId, String period) {
    }

    private final IDashboardRepository repo;

    /** @param repo port thống kê dashboard */
    public GetRevenueByCampaignUseCase(IDashboardRepository repo) {
        this.repo = repo;
    }

    /**
     * Lấy top doanh thu theo chiến dịch trong kỳ hiện tại.
     *
     * @param q phạm vi + kỳ
     * @return danh sách xếp hạng
     */
    @Override
    public List<RankedItem> execute(Query q) {
        return repo.revenueByCampaign(q.ownerId(), PeriodRanges.current(q.period()));
    }
}
