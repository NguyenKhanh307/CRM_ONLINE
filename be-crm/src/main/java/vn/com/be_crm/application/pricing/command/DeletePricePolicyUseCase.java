package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa chính sách giá. */
public class DeletePricePolicyUseCase implements IUseCase<Long, Void> {
    private final IPricePolicyRepository repo;
    /** @param repo port lưu trữ */
    public DeletePricePolicyUseCase(IPricePolicyRepository repo) { this.repo = repo; }
    /** Xóa PricePolicy. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("PricePolicy not found: " + id));
        repo.deleteById(id); return null;
    }
}
