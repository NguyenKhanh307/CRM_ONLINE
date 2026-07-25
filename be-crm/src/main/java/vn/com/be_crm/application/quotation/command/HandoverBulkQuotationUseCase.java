package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.shared.dto.HandoverBulkCommand;
import vn.com.be_crm.application.shared.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/**
 * Use case bàn giao hàng loạt báo giá sang người dùng khác.
 */
public class HandoverBulkQuotationUseCase {

    private final IQuotationRepository repository;
    private final NotifyAssignmentUseCase notifyUC;

    /** @param repository port lưu trữ Quotation @param notifyUC báo cho người nhận bàn giao */
    public HandoverBulkQuotationUseCase(IQuotationRepository repository, NotifyAssignmentUseCase notifyUC) {
        this.repository = repository;
        this.notifyUC = notifyUC;
    }

    /**
     * Bàn giao danh sách báo giá sang owner mới.
     *
     * @param cmd command chứa ids, toUserId, currentUserId, adminOrManager
     */
    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
        notifyUC.notifyHandover("quotation", "báo giá", cmd.getToUserId(),
                cmd.getIds() == null ? 0 : cmd.getIds().size());
    }
}
