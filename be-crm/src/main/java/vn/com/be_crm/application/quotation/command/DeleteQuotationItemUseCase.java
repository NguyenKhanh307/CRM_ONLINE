package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// xóa dòng hàng báo giá — tổng chứng từ tự cập nhật vì được tính lúc đọc, không cần tính lại
public class DeleteQuotationItemUseCase implements IUseCase<Long, Void> {
    private final IQuotationItemRepository repo;

    public DeleteQuotationItemUseCase(IQuotationItemRepository repo) {
        this.repo = repo;
    }

    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("QuotationItem not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
