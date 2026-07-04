package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm chiến dịch. */
public class DeleteCampaignUseCase implements IUseCase<DeleteCommand, Void> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public DeleteCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Xóa mềm Campaign. @param cmd @return null @throws NotFoundException */
    @Override public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Campaign not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy()); return null;
    }
}
