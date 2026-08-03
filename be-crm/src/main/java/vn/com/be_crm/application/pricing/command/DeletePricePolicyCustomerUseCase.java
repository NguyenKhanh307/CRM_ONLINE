package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa khách hàng trong chính sách giá. */
public class DeletePricePolicyCustomerUseCase implements IUseCase<Long, Void> {
    private final IPricePolicyCustomerRepository repo;
    /** @param repo port lưu trữ */
    public DeletePricePolicyCustomerUseCase(IPricePolicyCustomerRepository repo) { this.repo = repo; }
    /** Xóa PricePolicyCustomer. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("PricePolicyCustomer not found: " + id));
        repo.deleteById(id); return null;
    }
}
