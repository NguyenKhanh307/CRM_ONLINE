package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm đơn hàng. */
public class DeleteOrderUseCase implements IUseCase<DeleteCommand, Void> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOrderUseCase(IOrderRepository repo) { this.repo = repo; }
    /** Xóa mềm Order. @param cmd @return null @throws NotFoundException */
    @Override public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Order not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy()); return null;
    }
}
