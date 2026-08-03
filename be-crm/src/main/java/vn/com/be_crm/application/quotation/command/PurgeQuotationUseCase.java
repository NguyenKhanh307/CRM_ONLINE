package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/** Use case ẩn báo giá khỏi thùng rác. */
public class PurgeQuotationUseCase implements IUseCase<Long, Void> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public PurgeQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Set is_purged = true. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
