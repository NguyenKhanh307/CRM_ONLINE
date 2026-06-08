package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm cơ hội bán hàng. */
public class DeleteOpportunityUseCase implements IUseCase<Long, Void> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Xóa mềm Opportunity theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("Opportunity not found: " + id));
        repo.deleteById(id); return null;
    }
}
