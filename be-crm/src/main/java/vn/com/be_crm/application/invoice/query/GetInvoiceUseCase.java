package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy đơn hàng theo ID. */
public class GetInvoiceUseCase implements IUseCase<Long, InvoiceResult> {
    private final IInvoiceRepository repo;
    /** @param repo port lưu trữ */
    public GetInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }
    /** Lấy Invoice theo ID. @param id @return InvoiceResult @throws NotFoundException */
    @Override public InvoiceResult execute(Long id) {
        return InvoiceCommandMapper.toResult(repo.findById(id).orElseThrow(() -> new NotFoundException("Invoice not found: " + id)));
    }
}
