package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm tiềm năng. */
public class DeleteLeadUseCase implements IUseCase<Long, Void> {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public DeleteLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Xóa mềm Lead theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("Lead not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
