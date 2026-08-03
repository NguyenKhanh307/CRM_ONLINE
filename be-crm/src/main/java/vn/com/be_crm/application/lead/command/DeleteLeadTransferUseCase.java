package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// xóa bản ghi chuyển giao tiềm năng
public class DeleteLeadTransferUseCase implements IUseCase<Long, Void> {
    private final ILeadTransferRepository repo;

    public DeleteLeadTransferUseCase(ILeadTransferRepository repo) { this.repo = repo; }

    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("LeadTransfer not found: " + id));
        repo.deleteById(id); return null;
    }
}
