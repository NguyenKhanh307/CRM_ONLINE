package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

/** Use case ẩn tiềm năng khỏi thùng rác (DB vẫn giữ soft-delete). */
public class PurgeLeadUseCase implements IUseCase<Long, Void> {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public PurgeLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Set is_purged = true để ẩn khỏi thùng rác.
     * @param id ID cần ẩn @return null
     */
    @Override
    public Void execute(Long id) { repo.purgeById(id); return null; }
}
