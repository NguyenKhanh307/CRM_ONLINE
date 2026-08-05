package vn.com.be_crm.application.order.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// xóa dòng hàng đơn hàng — tổng chứng từ tự cập nhật vì được tính lúc đọc, không cần tính lại
public class DeleteOrderItemUseCase implements IUseCase<Long, Void> {
    private final IOrderItemRepository repo;

    public DeleteOrderItemUseCase(IOrderItemRepository repo) {
        this.repo = repo;
    }

    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("OrderItem not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
