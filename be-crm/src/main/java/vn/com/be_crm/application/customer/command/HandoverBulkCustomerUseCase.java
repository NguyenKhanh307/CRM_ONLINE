package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.core.dto.handover.HandoverBulkCommand;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

/**
 * Use case bàn giao hàng loạt khách hàng sang người dùng khác.
 */
public class HandoverBulkCustomerUseCase {

    private final ICustomerRepository repository;
    private final NotifyAssignmentUseCase notifyUC;

    /** @param repository port lưu trữ Customer @param notifyUC báo cho người nhận bàn giao */
    public HandoverBulkCustomerUseCase(ICustomerRepository repository, NotifyAssignmentUseCase notifyUC) {
        this.repository = repository;
        this.notifyUC = notifyUC;
    }

    /**
     * Bàn giao danh sách khách hàng sang owner mới.
     *
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
        notifyUC.notifyHandover("customer", "khách hàng", cmd.getToUserId(),
                cmd.getIds() == null ? 0 : cmd.getIds().size());
    }
}
