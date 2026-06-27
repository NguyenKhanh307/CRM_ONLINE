package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRevenueRecordRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa bản ghi doanh thu đơn hàng. */
public class DeleteInvoiceRevenueRecordUseCase implements IUseCase<Long, Void> {
    private final IInvoiceRevenueRecordRepository repo;
    /** @param repo port lưu trữ */
    public DeleteInvoiceRevenueRecordUseCase(IInvoiceRevenueRecordRepository repo) { this.repo = repo; }
    /** Xóa InvoiceRevenueRecord. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("InvoiceRevenueRecord not found: " + id));
        repo.deleteById(id); return null;
    }
}
