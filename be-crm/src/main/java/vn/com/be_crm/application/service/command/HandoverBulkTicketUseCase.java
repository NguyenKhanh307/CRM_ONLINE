package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

/**
 * Use case bàn giao hàng loạt phiếu sang nhân viên xử lý khác.
 */
public class HandoverBulkTicketUseCase {
    private final ITicketRepository repository;
    /** @param repository port lưu trữ Ticket */
    public HandoverBulkTicketUseCase(ITicketRepository repository) { this.repository = repository; }

    /**
     * Bàn giao danh sách phiếu sang người xử lý mới.
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
    }
}
