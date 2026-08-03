package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

// khôi phục tiềm năng từ thùng rác (xóa deleted_at, deleted_by, is_purged)
public class RestoreLeadUseCase implements IUseCase<Long, Void> {
    private final ILeadRepository repo;

    public RestoreLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    @Override
    public Void execute(Long id) { repo.restoreById(id); return null; }
}
