package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.UpdateInvoiceItemCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceItemResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// cập nhật dòng hàng hóa đơn. Thành tiền không lưu DB — tính lúc đọc (InvoiceItemCommandMapper.toResult)
public class UpdateInvoiceItemUseCase implements IUseCase<UpdateInvoiceItemCommand, InvoiceItemResult> {
    private final IInvoiceItemRepository repo;

    public UpdateInvoiceItemUseCase(IInvoiceItemRepository repo) {
        this.repo = repo;
    }

    @Override public InvoiceItemResult execute(UpdateInvoiceItemCommand cmd) {
        InvoiceItem existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("InvoiceItem not found: " + cmd.getId()));
        InvoiceItem saved = repo.save(InvoiceItemCommandMapper.toEntity(cmd, existing));
        return InvoiceItemCommandMapper.toResult(saved);
    }
}
