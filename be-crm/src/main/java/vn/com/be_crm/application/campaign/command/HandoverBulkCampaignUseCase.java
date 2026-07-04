package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

/**
 * Use case bàn giao hàng loạt chiến dịch sang người dùng khác.
 */
public class HandoverBulkCampaignUseCase {

    private final ICampaignRepository repository;

    /** @param repository port lưu trữ Campaign */
    public HandoverBulkCampaignUseCase(ICampaignRepository repository) {
        this.repository = repository;
    }

    /**
     * Bàn giao danh sách chiến dịch sang owner mới.
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
    }
}
