package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.dto.UpdateInvoiceCommand;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật đơn hàng. */
public class UpdateInvoiceUseCase implements IUseCase<UpdateInvoiceCommand, InvoiceResult> {
    private final IInvoiceRepository repo;
    /** @param repo port lưu trữ */
    public UpdateInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }
    /** Cập nhật Invoice. @param cmd @return InvoiceResult @throws NotFoundException */
    @Override public InvoiceResult execute(UpdateInvoiceCommand cmd) {
        Invoice e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Invoice not found: " + cmd.getId()));
        return InvoiceCommandMapper.toResult(repo.save(InvoiceCommandMapper.toEntity(cmd, e)));
    }
}
