package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.CreateCampaignCommand;
import vn.com.be_crm.application.campaign.dto.CampaignResult;
import vn.com.be_crm.application.campaign.mapper.CampaignCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

/** Use case tạo mới chiến dịch. */
public class CreateCampaignUseCase implements IUseCase<CreateCampaignCommand, CampaignResult> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public CreateCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Tạo mới Campaign. @param cmd @return CampaignResult */
    @Override public CampaignResult execute(CreateCampaignCommand cmd) {
        return CampaignCommandMapper.toResult(repo.save(CampaignCommandMapper.toEntity(cmd)));
    }
}
