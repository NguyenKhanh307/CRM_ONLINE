package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa đợt thanh toán. */
public class DeleteInvoicePaymentScheduleUseCase implements IUseCase<Long, Void> {
    private final IInvoicePaymentScheduleRepository repo;
    /** @param repo port lưu trữ */
    public DeleteInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository repo) { this.repo = repo; }
    /** Xóa InvoicePaymentSchedule. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("InvoicePaymentSchedule not found: " + id));
        repo.deleteById(id); return null;
    }
}
