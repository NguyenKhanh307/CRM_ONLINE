package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa dòng đơn hàng. */
public class DeleteOrderItemUseCase implements IUseCase<Long, Void> {
    private final IOrderItemRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOrderItemUseCase(IOrderItemRepository repo) { this.repo = repo; }
    /** Xóa OrderItem. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("OrderItem not found: " + id));
        repo.deleteById(id); return null;
    }
}
