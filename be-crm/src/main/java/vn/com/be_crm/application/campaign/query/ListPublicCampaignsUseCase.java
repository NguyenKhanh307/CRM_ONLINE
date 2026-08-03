package vn.com.be_crm.application.campaign.query;

import vn.com.be_crm.application.campaign.dto.PublicCampaignResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Liệt kê chiến dịch đang chạy cho landing page công khai (web tracking).
 *
 * <p>Chỉ trả chiến dịch <b>đang phát sinh lưu lượng</b> ({@code running}/{@code scheduled}) và
 * chỉ 3 trường id/mã/tên — khách ẩn danh không được thấy ngân sách, chi phí hay người phụ trách.
 * Chiến dịch đã kết thúc/hủy không xuất hiện vì không còn nguồn nào để quy về.</p>
 */
public class ListPublicCampaignsUseCase {

    /** Trạng thái được coi là "đang phục vụ lưu lượng". */
    private static final Set<CampaignStatus> LIVE = EnumSet.of(CampaignStatus.running, CampaignStatus.scheduled);

    /** Số chiến dịch tối đa nạp để lọc — dropdown demo không cần phân trang. */
    private static final int MAX = 200;

    private final ICampaignRepository repo;

    /** @param repo port lưu trữ chiến dịch */
    public ListPublicCampaignsUseCase(ICampaignRepository repo) {
        this.repo = repo;
    }

    /**
     * @return danh sách chiến dịch đang chạy, mới nhất trước
     */
    public List<PublicCampaignResult> execute() {
        var page = repo.findAll(PageRequest.builder()
                .page(0).size(MAX).sortBy("createdAt").sortDir("desc").build());
        return page.getItems().stream()
                .filter(c -> LIVE.contains(c.getStatus()))
                .map(this::toResult)
                .toList();
    }

    /** Chuyển entity sang DTO rút gọn. */
    private PublicCampaignResult toResult(Campaign c) {
        return new PublicCampaignResult(c.getId(), c.getCode(), c.getName());
    }
}
