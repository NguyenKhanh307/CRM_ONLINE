package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

/** Use case khôi phục đơn hàng từ thùng rác. */
public class RestoreInvoiceUseCase implements IUseCase<Long, Void> {
    private final IInvoiceRepository repo;
    /** @param repo port lưu trữ */
    public RestoreInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }
    /** Khôi phục Invoice. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
