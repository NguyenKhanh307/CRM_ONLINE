package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.CreateCampaignMemberCommand;
import vn.com.be_crm.application.campaign.dto.CampaignMemberResult;
import vn.com.be_crm.application.campaign.mapper.CampaignMemberCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignMemberRepository;

/** Use case tạo mới thành viên chiến dịch. */
public class CreateCampaignMemberUseCase implements IUseCase<CreateCampaignMemberCommand, CampaignMemberResult> {
    private final ICampaignMemberRepository repo;
    /** @param repo port lưu trữ */
    public CreateCampaignMemberUseCase(ICampaignMemberRepository repo) { this.repo = repo; }
    /** Tạo mới CampaignMember. @param cmd @return CampaignMemberResult */
    @Override public CampaignMemberResult execute(CreateCampaignMemberCommand cmd) {
        return CampaignMemberCommandMapper.toResult(repo.save(CampaignMemberCommandMapper.toEntity(cmd)));
    }
}
