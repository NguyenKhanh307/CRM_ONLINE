package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa dòng hàng đơn hàng. Tổng chứng từ được tính lại sau khi xóa. */
public class DeleteOrderItemUseCase implements IUseCase<Long, Void> {
    private final IOrderItemRepository repo;
    private final RecomputeOrderTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public DeleteOrderItemUseCase(IOrderItemRepository repo, RecomputeOrderTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Xóa OrderItem rồi tính lại tổng chứng từ. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        OrderItem e = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("OrderItem not found: " + id));
        Long parentId = e.getOrderId();
        repo.deleteById(id);
        recomputeUC.execute(parentId);
        return null;
    }
}
