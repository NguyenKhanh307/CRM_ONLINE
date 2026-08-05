package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// xóa dòng hàng hóa đơn — tổng chứng từ tự cập nhật vì được tính lúc đọc, không cần tính lại
public class DeleteInvoiceItemUseCase implements IUseCase<Long, Void> {
    private final IInvoiceItemRepository repo;

    public DeleteInvoiceItemUseCase(IInvoiceItemRepository repo) {
        this.repo = repo;
    }

    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("InvoiceItem not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
