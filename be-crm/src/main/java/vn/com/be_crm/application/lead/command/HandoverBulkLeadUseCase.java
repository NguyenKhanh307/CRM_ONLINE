package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.application.shared.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

/**
 * Use case bàn giao hàng loạt tiềm năng sang người dùng khác.
 */
public class HandoverBulkLeadUseCase {

    private final ILeadRepository repository;
    private final NotifyAssignmentUseCase notifyUC;

    /** @param repository port lưu trữ Lead @param notifyUC báo cho người nhận bàn giao */
    public HandoverBulkLeadUseCase(ILeadRepository repository, NotifyAssignmentUseCase notifyUC) {
        this.repository = repository;
        this.notifyUC = notifyUC;
    }

    /**
     * Bàn giao danh sách tiềm năng sang owner mới.
     *
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
        notifyUC.notifyHandover("lead", "tiềm năng", cmd.getToUserId(),
                cmd.getIds() == null ? 0 : cmd.getIds().size());
    }
}
