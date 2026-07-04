package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

/** Use case ẩn chiến dịch khỏi thùng rác. */
public class PurgeCampaignUseCase implements IUseCase<Long, Void> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public PurgeCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Set is_purged = true. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
