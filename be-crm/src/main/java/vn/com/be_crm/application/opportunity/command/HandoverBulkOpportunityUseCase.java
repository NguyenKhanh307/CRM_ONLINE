package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.core.dto.handover.HandoverBulkCommand;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

/**
 * Use case bàn giao hàng loạt cơ hội sang người dùng khác.
 */
public class HandoverBulkOpportunityUseCase {

    private final IOpportunityRepository repository;
    private final NotifyAssignmentUseCase notifyUC;

    /** @param repository port lưu trữ Opportunity @param notifyUC báo cho người nhận bàn giao */
    public HandoverBulkOpportunityUseCase(IOpportunityRepository repository, NotifyAssignmentUseCase notifyUC) {
        this.repository = repository;
        this.notifyUC = notifyUC;
    }

    /**
     * Bàn giao danh sách cơ hội sang owner mới.
     *
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
        notifyUC.notifyHandover("opportunity", "cơ hội", cmd.getToUserId(),
                cmd.getIds() == null ? 0 : cmd.getIds().size());
    }
}
