package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa sản phẩm trong chính sách giá. */
public class DeletePricePolicyProductUseCase implements IUseCase<Long, Void> {
    private final IPricePolicyProductRepository repo;
    /** @param repo port lưu trữ */
    public DeletePricePolicyProductUseCase(IPricePolicyProductRepository repo) { this.repo = repo; }
    /** Xóa PricePolicyProduct. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("PricePolicyProduct not found: " + id));
        repo.deleteById(id); return null;
    }
}
