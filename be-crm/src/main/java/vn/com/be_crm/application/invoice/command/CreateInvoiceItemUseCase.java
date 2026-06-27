package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.CreateInvoiceItemCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceItemResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;

/** Use case tạo mới dòng đơn hàng. */
public class CreateInvoiceItemUseCase implements IUseCase<CreateInvoiceItemCommand, InvoiceItemResult> {
    private final IInvoiceItemRepository repo;
    /** @param repo port lưu trữ */
    public CreateInvoiceItemUseCase(IInvoiceItemRepository repo) { this.repo = repo; }
    /** Tạo mới InvoiceItem. @param cmd @return InvoiceItemResult */
    @Override public InvoiceItemResult execute(CreateInvoiceItemCommand cmd) {
        return InvoiceItemCommandMapper.toResult(repo.save(InvoiceItemCommandMapper.toEntity(cmd)));
    }
}
