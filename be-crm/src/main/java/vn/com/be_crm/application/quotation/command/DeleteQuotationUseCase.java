package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm báo giá. */
public class DeleteQuotationUseCase implements IUseCase<Long, Void> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public DeleteQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Xóa mềm Quotation. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("Quotation not found: " + id));
        repo.deleteById(id); return null;
    }
}
