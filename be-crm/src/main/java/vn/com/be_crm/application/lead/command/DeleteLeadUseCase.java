package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// xóa mềm tiềm năng
public class DeleteLeadUseCase implements IUseCase<DeleteCommand, Void> {
    private final ILeadRepository repo;

    public DeleteLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    @Override
    public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Lead not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy());
        return null;
    }
}
