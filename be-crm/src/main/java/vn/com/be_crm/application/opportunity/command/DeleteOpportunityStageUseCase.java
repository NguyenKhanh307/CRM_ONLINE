package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa giai đoạn pipeline. */
public class DeleteOpportunityStageUseCase implements IUseCase<Long, Void> {
    private final IOpportunityStageRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOpportunityStageUseCase(IOpportunityStageRepository repo) { this.repo = repo; }
    /** @param id ID @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("OpportunityStage", id));
        repo.deleteById(id); return null;
    }
}
