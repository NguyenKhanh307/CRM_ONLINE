package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa nhân viên trong chính sách giá. */
public class DeletePricePolicyEmployeeUseCase implements IUseCase<Long, Void> {
    private final IPricePolicyEmployeeRepository repo;
    /** @param repo port lưu trữ */
    public DeletePricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository repo) { this.repo = repo; }
    /** Xóa PricePolicyEmployee. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("PricePolicyEmployee not found: " + id));
        repo.deleteById(id); return null;
    }
}
