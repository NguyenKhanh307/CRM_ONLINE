package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

/**
 * Use case bàn giao hàng loạt cơ hội sang người dùng khác.
 */
public class HandoverBulkOpportunityUseCase {

    private final IOpportunityRepository repository;

    /** @param repository port lưu trữ Opportunity */
    public HandoverBulkOpportunityUseCase(IOpportunityRepository repository) {
        this.repository = repository;
    }

    /**
     * Bàn giao danh sách cơ hội sang owner mới.
     *
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
    }
}
