package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.core.dto.handover.HandoverBulkCommand;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

// bàn giao hàng loạt tiềm năng sang người dùng khác
public class HandoverBulkLeadUseCase {

    private final ILeadRepository repository;
    private final NotifyAssignmentUseCase notifyUC;

    public HandoverBulkLeadUseCase(ILeadRepository repository, NotifyAssignmentUseCase notifyUC) {
        this.repository = repository;
        this.notifyUC = notifyUC;
    }

    public void execute(HandoverBulkCommand cmd) {
        repository.handoverBulk(cmd.getIds(), cmd.getToUserId(), cmd.getCurrentUserId(), cmd.isAdminOrManager());
        notifyUC.notifyHandover("lead", "tiềm năng", cmd.getToUserId(),
                cmd.getIds() == null ? 0 : cmd.getIds().size());
    }
}
