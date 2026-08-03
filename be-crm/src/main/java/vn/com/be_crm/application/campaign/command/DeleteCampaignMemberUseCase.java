package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignMemberRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa thành viên chiến dịch. */
public class DeleteCampaignMemberUseCase implements IUseCase<Long, Void> {
    private final ICampaignMemberRepository repo;
    /** @param repo port lưu trữ */
    public DeleteCampaignMemberUseCase(ICampaignMemberRepository repo) { this.repo = repo; }
    /** Xóa CampaignMember. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("CampaignMember not found: " + id));
        repo.deleteById(id); return null;
    }
}
