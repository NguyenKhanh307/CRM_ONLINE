package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

/**
 * Use case bàn giao hàng loạt đơn hàng sang người dùng khác.
 */
public class HandoverBulkOrderUseCase {

    private final IOrderRepository repository;

    /** @param repository port lưu trữ Order */
    public HandoverBulkOrderUseCase(IOrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Bàn giao danh sách đơn hàng sang owner mới.
     *
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
    }
}
