package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm khách hàng. */
public class DeleteCustomerUseCase implements IUseCase<Long, Void> {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public DeleteCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }

    /**
     * Xóa mềm Customer theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("Customer not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
