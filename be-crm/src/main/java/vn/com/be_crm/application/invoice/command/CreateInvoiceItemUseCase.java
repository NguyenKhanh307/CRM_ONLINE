package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.CreateInvoiceItemCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceItemResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;

// tạo mới dòng hàng hóa đơn. Thành tiền không lưu DB — tính lúc đọc (InvoiceItemCommandMapper.toResult)
public class CreateInvoiceItemUseCase implements IUseCase<CreateInvoiceItemCommand, InvoiceItemResult> {
    private final IInvoiceItemRepository repo;

    public CreateInvoiceItemUseCase(IInvoiceItemRepository repo) {
        this.repo = repo;
    }

    @Override public InvoiceItemResult execute(CreateInvoiceItemCommand cmd) {
        InvoiceItem saved = repo.save(InvoiceItemCommandMapper.toEntity(cmd));
        return InvoiceItemCommandMapper.toResult(saved);
    }
}
