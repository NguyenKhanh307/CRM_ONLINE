package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

/** Use case ẩn đơn hàng khỏi thùng rác. */
public class PurgeInvoiceUseCase implements IUseCase<Long, Void> {
    private final IInvoiceRepository repo;
    /** @param repo port lưu trữ */
    public PurgeInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }
    /** Set is_purged = true. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
