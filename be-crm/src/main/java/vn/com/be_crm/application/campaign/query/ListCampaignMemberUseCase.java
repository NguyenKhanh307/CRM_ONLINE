package vn.com.be_crm.application.campaign.query;

import vn.com.be_crm.application.campaign.dto.CampaignMemberResult;
import vn.com.be_crm.application.campaign.mapper.CampaignMemberCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignMemberRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách thành viên chiến dịch theo campaignId. */
public class ListCampaignMemberUseCase implements IUseCase<Long, List<CampaignMemberResult>> {
    private final ICampaignMemberRepository repo;
    /** @param repo port lưu trữ */
    public ListCampaignMemberUseCase(ICampaignMemberRepository repo) { this.repo = repo; }
    /** Lấy danh sách CampaignMember theo campaignId. @param campaignId @return danh sách */
    @Override public List<CampaignMemberResult> execute(Long campaignId) {
        return repo.findAllByCampaignId(campaignId).stream().map(CampaignMemberCommandMapper::toResult).collect(Collectors.toList());
    }
}
