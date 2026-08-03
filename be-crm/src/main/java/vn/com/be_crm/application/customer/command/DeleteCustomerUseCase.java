package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa mềm khách hàng. */
public class DeleteCustomerUseCase implements IUseCase<DeleteCommand, Void> {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public DeleteCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }

    /**
     * Xóa mềm Customer theo ID, ghi nhận người xóa.
     * @param cmd command chứa id và deletedBy @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Customer not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy());
        return null;
    }
}
