package vn.com.be_crm.application.service.command;

import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa mềm phiếu. */
public class DeleteTicketUseCase implements IUseCase<DeleteCommand, Void> {
    private final ITicketRepository repo;
    /** @param repo port lưu trữ */
    public DeleteTicketUseCase(ITicketRepository repo) { this.repo = repo; }
    /** Xóa mềm Ticket. @param cmd @return null @throws NotFoundException */
    @Override public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Ticket not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy()); return null;
    }
}
