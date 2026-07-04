package vn.com.be_crm.application.campaign.query;

import vn.com.be_crm.application.campaign.dto.CampaignResult;
import vn.com.be_crm.application.campaign.mapper.CampaignCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách chiến dịch có phân trang. */
public class ListCampaignUseCase implements IUseCase<PageRequest, PageResult<CampaignResult>> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public ListCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Lấy danh sách Campaign. @param r phân trang @return PageResult */
    @Override public PageResult<CampaignResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<CampaignResult>builder()
                .items(page.getItems().stream().map(CampaignCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
