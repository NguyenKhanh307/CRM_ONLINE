package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

/**
 * Use case bàn giao hàng loạt khách hàng sang người dùng khác.
 */
public class HandoverBulkCustomerUseCase {

    private final ICustomerRepository repository;

    /** @param repository port lưu trữ Customer */
    public HandoverBulkCustomerUseCase(ICustomerRepository repository) {
        this.repository = repository;
    }

    /**
     * Bàn giao danh sách khách hàng sang owner mới.
     *
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
    }
}
