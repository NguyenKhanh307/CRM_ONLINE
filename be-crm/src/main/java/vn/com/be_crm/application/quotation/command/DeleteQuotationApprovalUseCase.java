package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa bước phê duyệt báo giá. */
public class DeleteQuotationApprovalUseCase implements IUseCase<Long, Void> {
    private final IQuotationApprovalRepository repo;
    /** @param repo port lưu trữ */
    public DeleteQuotationApprovalUseCase(IQuotationApprovalRepository repo) { this.repo = repo; }
    /** Xóa QuotationApproval. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("QuotationApproval not found: " + id));
        repo.deleteById(id); return null;
    }
}
