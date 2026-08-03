package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

/** Use case ẩn cơ hội khỏi thùng rác. */
public class PurgeOpportunityUseCase implements IUseCase<Long, Void> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public PurgeOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }
    /** Set is_purged = true. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
