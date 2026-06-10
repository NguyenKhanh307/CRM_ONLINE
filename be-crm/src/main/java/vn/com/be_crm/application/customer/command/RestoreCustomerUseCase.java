package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

/** Use case khôi phục khách hàng từ thùng rác. */
public class RestoreCustomerUseCase implements IUseCase<Long, Void> {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public RestoreCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }
    /** Khôi phục Customer. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
