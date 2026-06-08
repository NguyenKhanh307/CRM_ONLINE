package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductTypeRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa loại sản phẩm trong chính sách giá. */
public class DeletePricePolicyProductTypeUseCase implements IUseCase<Long, Void> {
    private final IPricePolicyProductTypeRepository repo;
    /** @param repo port lưu trữ */
    public DeletePricePolicyProductTypeUseCase(IPricePolicyProductTypeRepository repo) { this.repo = repo; }
    /** Xóa PricePolicyProductType. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("PricePolicyProductType not found: " + id));
        repo.deleteById(id); return null;
    }
}
