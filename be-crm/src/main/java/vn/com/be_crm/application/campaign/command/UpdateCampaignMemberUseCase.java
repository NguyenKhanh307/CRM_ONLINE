package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.CampaignMemberResult;
import vn.com.be_crm.application.campaign.dto.UpdateCampaignMemberCommand;
import vn.com.be_crm.application.campaign.mapper.CampaignMemberCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.entity.CampaignMember;
import vn.com.be_crm.domain.campaign.repository.ICampaignMemberRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case cập nhật thành viên chiến dịch. */
public class UpdateCampaignMemberUseCase implements IUseCase<UpdateCampaignMemberCommand, CampaignMemberResult> {
    private final ICampaignMemberRepository repo;
    /** @param repo port lưu trữ */
    public UpdateCampaignMemberUseCase(ICampaignMemberRepository repo) { this.repo = repo; }
    /** Cập nhật CampaignMember. @param cmd @return CampaignMemberResult @throws NotFoundException */
    @Override public CampaignMemberResult execute(UpdateCampaignMemberCommand cmd) {
        CampaignMember e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("CampaignMember not found: " + cmd.getId()));
        return CampaignMemberCommandMapper.toResult(repo.save(CampaignMemberCommandMapper.toEntity(cmd, e)));
    }
}
