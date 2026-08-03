package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

/** Use case khôi phục cơ hội từ thùng rác. */
public class RestoreOpportunityUseCase implements IUseCase<Long, Void> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public RestoreOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }
    /** Khôi phục Opportunity. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
