package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

/** Use case ẩn khách hàng khỏi thùng rác. */
public class PurgeCustomerUseCase implements IUseCase<Long, Void> {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public PurgeCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }
    /** Set is_purged = true. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
