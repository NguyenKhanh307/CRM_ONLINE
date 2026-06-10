package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

/** Use case khôi phục tiềm năng từ thùng rác. */
public class RestoreLeadUseCase implements IUseCase<Long, Void> {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public RestoreLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Khôi phục Lead (xóa deleted_at, deleted_by, is_purged).
     * @param id ID cần khôi phục @return null
     */
    @Override
    public Void execute(Long id) { repo.restoreById(id); return null; }
}
