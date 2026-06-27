package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa dòng đơn hàng. */
public class DeleteInvoiceItemUseCase implements IUseCase<Long, Void> {
    private final IInvoiceItemRepository repo;
    /** @param repo port lưu trữ */
    public DeleteInvoiceItemUseCase(IInvoiceItemRepository repo) { this.repo = repo; }
    /** Xóa InvoiceItem. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("InvoiceItem not found: " + id));
        repo.deleteById(id); return null;
    }
}
