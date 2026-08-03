package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa mềm đơn hàng. */
public class DeleteInvoiceUseCase implements IUseCase<DeleteCommand, Void> {
    private final IInvoiceRepository repo;
    /** @param repo port lưu trữ */
    public DeleteInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }
    /** Xóa mềm Invoice. @param cmd @return null @throws NotFoundException */
    @Override public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Invoice not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy()); return null;
    }
}
