package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerCategoryRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa danh mục khách hàng trong chính sách giá. */
public class DeletePricePolicyCustomerCategoryUseCase implements IUseCase<Long, Void> {
    private final IPricePolicyCustomerCategoryRepository repo;
    /** @param repo port lưu trữ */
    public DeletePricePolicyCustomerCategoryUseCase(IPricePolicyCustomerCategoryRepository repo) { this.repo = repo; }
    /** Xóa PricePolicyCustomerCategory. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("PricePolicyCustomerCategory not found: " + id));
        repo.deleteById(id); return null;
    }
}
