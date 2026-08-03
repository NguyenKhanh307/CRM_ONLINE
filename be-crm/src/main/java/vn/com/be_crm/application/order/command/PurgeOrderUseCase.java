package vn.com.be_crm.application.order.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

/** Use case ẩn đơn hàng khỏi thùng rác. */
public class PurgeOrderUseCase implements IUseCase<Long, Void> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public PurgeOrderUseCase(IOrderRepository repo) { this.repo = repo; }
    /** Set is_purged = true. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
