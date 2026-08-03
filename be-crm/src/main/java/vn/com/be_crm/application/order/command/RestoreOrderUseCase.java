package vn.com.be_crm.application.order.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

/** Use case khôi phục đơn hàng từ thùng rác. */
public class RestoreOrderUseCase implements IUseCase<Long, Void> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public RestoreOrderUseCase(IOrderRepository repo) { this.repo = repo; }
    /** Khôi phục Order. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
