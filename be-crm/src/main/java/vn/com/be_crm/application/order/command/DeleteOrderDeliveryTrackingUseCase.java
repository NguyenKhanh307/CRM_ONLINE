package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa theo dõi giao hàng. */
public class DeleteOrderDeliveryTrackingUseCase implements IUseCase<Long, Void> {
    private final IOrderDeliveryTrackingRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOrderDeliveryTrackingUseCase(IOrderDeliveryTrackingRepository repo) { this.repo = repo; }
    /** Xóa OrderDeliveryTracking. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("OrderDeliveryTracking not found: " + id));
        repo.deleteById(id); return null;
    }
}
