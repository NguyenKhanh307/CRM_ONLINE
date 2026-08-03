package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.CreateLeadTransferCommand;
import vn.com.be_crm.application.lead.dto.LeadTransferResult;
import vn.com.be_crm.application.lead.mapper.LeadTransferCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;

// tạo mới bản ghi chuyển giao tiềm năng
public class CreateLeadTransferUseCase implements IUseCase<CreateLeadTransferCommand, LeadTransferResult> {
    private final ILeadTransferRepository repo;

    public CreateLeadTransferUseCase(ILeadTransferRepository repo) { this.repo = repo; }

    @Override
    public LeadTransferResult execute(CreateLeadTransferCommand cmd) {
        return LeadTransferCommandMapper.toResult(repo.save(LeadTransferCommandMapper.toEntity(cmd)));
    }
}
