package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm hàng hóa. */
public class DeleteProductUseCase implements IUseCase<Long, Void> {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public DeleteProductUseCase(IProductRepository repo) { this.repo = repo; }
    /**
     * Xóa mềm Product theo ID.
     * @param id ID cần xóa @return null @throws NotFoundException
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("Product", id));
        repo.deleteById(id);
        return null;
    }
}
