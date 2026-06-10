package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm báo giá. */
public class DeleteQuotationUseCase implements IUseCase<DeleteCommand, Void> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public DeleteQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Xóa mềm Quotation. @param cmd @return null @throws NotFoundException */
    @Override public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Quotation not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy()); return null;
    }
}
