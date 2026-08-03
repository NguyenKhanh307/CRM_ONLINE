package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa mềm cơ hội bán hàng. */
public class DeleteOpportunityUseCase implements IUseCase<DeleteCommand, Void> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Xóa mềm Opportunity theo ID, ghi nhận người xóa.
     * @param cmd command chứa id và deletedBy @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Opportunity not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy()); return null;
    }
}
