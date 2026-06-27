package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.InvoiceItemResult;
import vn.com.be_crm.application.invoice.dto.UpdateInvoiceItemCommand;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng đơn hàng. */
public class UpdateInvoiceItemUseCase implements IUseCase<UpdateInvoiceItemCommand, InvoiceItemResult> {
    private final IInvoiceItemRepository repo;
    /** @param repo port lưu trữ */
    public UpdateInvoiceItemUseCase(IInvoiceItemRepository repo) { this.repo = repo; }
    /** Cập nhật InvoiceItem. @param cmd @return InvoiceItemResult @throws NotFoundException */
    @Override public InvoiceItemResult execute(UpdateInvoiceItemCommand cmd) {
        InvoiceItem e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("InvoiceItem not found: " + cmd.getId()));
        return InvoiceItemCommandMapper.toResult(repo.save(InvoiceItemCommandMapper.toEntity(cmd, e)));
    }
}
