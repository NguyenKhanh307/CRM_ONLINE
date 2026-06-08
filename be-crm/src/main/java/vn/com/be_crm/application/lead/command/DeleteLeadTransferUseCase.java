package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa bản ghi chuyển giao tiềm năng. */
public class DeleteLeadTransferUseCase implements IUseCase<Long, Void> {
    private final ILeadTransferRepository repo;
    /** @param repo port lưu trữ */
    public DeleteLeadTransferUseCase(ILeadTransferRepository repo) { this.repo = repo; }

    /**
     * Xóa LeadTransfer theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("LeadTransfer not found: " + id));
        repo.deleteById(id); return null;
    }
}
