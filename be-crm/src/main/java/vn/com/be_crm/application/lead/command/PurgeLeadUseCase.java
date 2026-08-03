package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

// ẩn tiềm năng khỏi thùng rác (set is_purged=true, DB vẫn giữ soft-delete)
public class PurgeLeadUseCase implements IUseCase<Long, Void> {
    private final ILeadRepository repo;

    public PurgeLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    @Override
    public Void execute(Long id) { repo.purgeById(id); return null; }
}
