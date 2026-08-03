package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadTransferResult;
import vn.com.be_crm.application.lead.dto.UpdateLeadTransferCommand;
import vn.com.be_crm.application.lead.mapper.LeadTransferCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.lead.entity.LeadTransfer;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// cập nhật bản ghi chuyển giao tiềm năng
public class UpdateLeadTransferUseCase implements IUseCase<UpdateLeadTransferCommand, LeadTransferResult> {
    private final ILeadTransferRepository repo;

    public UpdateLeadTransferUseCase(ILeadTransferRepository repo) { this.repo = repo; }

    @Override
    public LeadTransferResult execute(UpdateLeadTransferCommand cmd) {
        LeadTransfer existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("LeadTransfer not found: " + cmd.getId()));
        return LeadTransferCommandMapper.toResult(repo.save(LeadTransferCommandMapper.toEntity(cmd, existing)));
    }
}
