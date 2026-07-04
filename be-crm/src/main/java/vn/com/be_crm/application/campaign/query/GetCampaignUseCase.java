package vn.com.be_crm.application.campaign.query;

import vn.com.be_crm.application.campaign.dto.CampaignResult;
import vn.com.be_crm.application.campaign.mapper.CampaignCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy chiến dịch theo ID. */
public class GetCampaignUseCase implements IUseCase<Long, CampaignResult> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public GetCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Lấy Campaign theo ID. @param id @return CampaignResult @throws NotFoundException */
    @Override public CampaignResult execute(Long id) {
        return CampaignCommandMapper.toResult(repo.findById(id).orElseThrow(() -> new NotFoundException("Campaign not found: " + id)));
    }
}
