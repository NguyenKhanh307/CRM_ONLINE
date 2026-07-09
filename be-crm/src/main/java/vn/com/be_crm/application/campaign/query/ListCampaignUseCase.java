package vn.com.be_crm.application.campaign.query;

import vn.com.be_crm.application.campaign.dto.CampaignResult;
import vn.com.be_crm.application.campaign.mapper.CampaignCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách chiến dịch có phân trang. */
public class ListCampaignUseCase implements IUseCase<PageRequest, PageResult<CampaignResult>> {
    private final ICampaignRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListCampaignUseCase(ICampaignRepository repo, INameResolver names) { this.repo = repo; this.names = names; }
    /** Lấy danh sách Campaign kèm tên người phụ trách. @param r phân trang @return PageResult */
    @Override public PageResult<CampaignResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<CampaignResult> items = page.getItems().stream().map(CampaignCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, CampaignResult::getOwnerId, names::users, CampaignResult::setOwnerName);
        return PageResult.<CampaignResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
