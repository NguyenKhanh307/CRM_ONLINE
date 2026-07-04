package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.CampaignResult;
import vn.com.be_crm.application.campaign.dto.UpdateCampaignCommand;
import vn.com.be_crm.application.campaign.mapper.CampaignCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật chiến dịch. */
public class UpdateCampaignUseCase implements IUseCase<UpdateCampaignCommand, CampaignResult> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public UpdateCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Cập nhật Campaign. @param cmd @return CampaignResult @throws NotFoundException */
    @Override public CampaignResult execute(UpdateCampaignCommand cmd) {
        Campaign e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Campaign not found: " + cmd.getId()));
        return CampaignCommandMapper.toResult(repo.save(CampaignCommandMapper.toEntity(cmd, e)));
    }
}
