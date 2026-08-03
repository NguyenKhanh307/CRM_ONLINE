package vn.com.be_crm.application.order.command;

import vn.com.be_crm.core.dto.handover.HandoverBulkCommand;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

/**
 * Use case bàn giao hàng loạt đơn hàng sang người dùng khác.
 */
public class HandoverBulkOrderUseCase {

    private final IOrderRepository repository;
    private final NotifyAssignmentUseCase notifyUC;

    /** @param repository port lưu trữ Order @param notifyUC báo cho người nhận bàn giao */
    public HandoverBulkOrderUseCase(IOrderRepository repository, NotifyAssignmentUseCase notifyUC) {
        this.repository = repository;
        this.notifyUC = notifyUC;
    }

    /**
     * Bàn giao danh sách đơn hàng sang owner mới.
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
        notifyUC.notifyHandover("order", "đơn hàng", cmd.getToUserId(),
                cmd.getIds() == null ? 0 : cmd.getIds().size());
    }
}
