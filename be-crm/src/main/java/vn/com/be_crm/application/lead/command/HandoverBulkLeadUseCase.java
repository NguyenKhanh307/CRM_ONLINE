package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

/**
 * Use case bàn giao hàng loạt tiềm năng sang người dùng khác.
 */
public class HandoverBulkLeadUseCase {

    private final ILeadRepository repository;

    /** @param repository port lưu trữ Lead */
    public HandoverBulkLeadUseCase(ILeadRepository repository) {
        this.repository = repository;
    }

    /**
     * Bàn giao danh sách tiềm năng sang owner mới.
     *
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
    }
}
