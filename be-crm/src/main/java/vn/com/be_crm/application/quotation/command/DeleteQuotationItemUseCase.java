package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa dòng báo giá. */
public class DeleteQuotationItemUseCase implements IUseCase<Long, Void> {
    private final IQuotationItemRepository repo;
    /** @param repo port lưu trữ */
    public DeleteQuotationItemUseCase(IQuotationItemRepository repo) { this.repo = repo; }
    /** Xóa QuotationItem. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("QuotationItem not found: " + id));
        repo.deleteById(id); return null;
    }
}
