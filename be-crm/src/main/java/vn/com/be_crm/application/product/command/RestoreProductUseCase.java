package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductRepository;

/** Use case khôi phục hàng hóa từ thùng rác. */
public class RestoreProductUseCase implements IUseCase<Long, Void> {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public RestoreProductUseCase(IProductRepository repo) { this.repo = repo; }
    /** Khôi phục Product. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
