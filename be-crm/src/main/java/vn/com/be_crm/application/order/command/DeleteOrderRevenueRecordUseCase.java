package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa bản ghi doanh thu đơn hàng. */
public class DeleteOrderRevenueRecordUseCase implements IUseCase<Long, Void> {
    private final IOrderRevenueRecordRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOrderRevenueRecordUseCase(IOrderRevenueRecordRepository repo) { this.repo = repo; }
    /** Xóa OrderRevenueRecord. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("OrderRevenueRecord not found: " + id));
        repo.deleteById(id); return null;
    }
}
