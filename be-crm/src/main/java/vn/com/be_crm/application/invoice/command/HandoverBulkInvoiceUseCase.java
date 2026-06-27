package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

/**
 * Use case bàn giao hàng loạt đơn hàng sang người dùng khác.
 */
public class HandoverBulkInvoiceUseCase {

    private final IInvoiceRepository repository;

    /** @param repository port lưu trữ Invoice */
    public HandoverBulkInvoiceUseCase(IInvoiceRepository repository) {
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
