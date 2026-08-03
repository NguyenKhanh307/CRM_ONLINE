package vn.com.be_crm.application.product.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductRepository;

/** Use case ẩn hàng hóa khỏi thùng rác. */
public class PurgeProductUseCase implements IUseCase<Long, Void> {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public PurgeProductUseCase(IProductRepository repo) { this.repo = repo; }
    /** Set is_purged = true. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
