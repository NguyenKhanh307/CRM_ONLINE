package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/** Use case khôi phục báo giá từ thùng rác. */
public class RestoreQuotationUseCase implements IUseCase<Long, Void> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public RestoreQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Khôi phục Quotation. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
