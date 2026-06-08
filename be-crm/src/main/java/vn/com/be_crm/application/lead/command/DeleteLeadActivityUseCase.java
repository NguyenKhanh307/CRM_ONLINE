package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadActivityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa hoạt động tiềm năng. */
public class DeleteLeadActivityUseCase implements IUseCase<Long, Void> {
    private final ILeadActivityRepository repo;
    /** @param repo port lưu trữ */
    public DeleteLeadActivityUseCase(ILeadActivityRepository repo) { this.repo = repo; }

    /**
     * Xóa LeadActivity theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("LeadActivity not found: " + id));
        repo.deleteById(id); return null;
    }
}
