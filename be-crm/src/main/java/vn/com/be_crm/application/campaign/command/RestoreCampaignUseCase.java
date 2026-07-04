package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

/** Use case khôi phục chiến dịch từ thùng rác. */
public class RestoreCampaignUseCase implements IUseCase<Long, Void> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public RestoreCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Khôi phục Campaign. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
