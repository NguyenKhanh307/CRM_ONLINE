package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductCategoryRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa danh mục hàng hóa. */
public class DeleteProductCategoryUseCase implements IUseCase<Long, Void> {
    private final IProductCategoryRepository repo;
    /** @param repo port lưu trữ */
    public DeleteProductCategoryUseCase(IProductCategoryRepository repo) { this.repo = repo; }
    /**
     * Xóa ProductCategory theo ID.
     * @param id ID cần xóa @return null @throws NotFoundException
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("ProductCategory", id));
        repo.deleteById(id);
        return null;
    }
}
