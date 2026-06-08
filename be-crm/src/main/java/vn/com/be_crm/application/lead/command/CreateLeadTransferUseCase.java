package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.CreateLeadTransferCommand;
import vn.com.be_crm.application.lead.dto.LeadTransferResult;
import vn.com.be_crm.application.lead.mapper.LeadTransferCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;

/** Use case tạo mới bản ghi chuyển giao tiềm năng. */
public class CreateLeadTransferUseCase implements IUseCase<CreateLeadTransferCommand, LeadTransferResult> {
    private final ILeadTransferRepository repo;
    /** @param repo port lưu trữ */
    public CreateLeadTransferUseCase(ILeadTransferRepository repo) { this.repo = repo; }

    /**
     * Tạo mới LeadTransfer và trả về result.
     * @param cmd dữ liệu tạo mới @return LeadTransferResult
     */
    @Override
    public LeadTransferResult execute(CreateLeadTransferCommand cmd) {
        return LeadTransferCommandMapper.toResult(repo.save(LeadTransferCommandMapper.toEntity(cmd)));
    }
}
