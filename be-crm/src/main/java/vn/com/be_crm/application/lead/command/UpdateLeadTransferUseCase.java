package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadTransferResult;
import vn.com.be_crm.application.lead.dto.UpdateLeadTransferCommand;
import vn.com.be_crm.application.lead.mapper.LeadTransferCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.entity.LeadTransfer;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật bản ghi chuyển giao tiềm năng. */
public class UpdateLeadTransferUseCase implements IUseCase<UpdateLeadTransferCommand, LeadTransferResult> {
    private final ILeadTransferRepository repo;
    /** @param repo port lưu trữ */
    public UpdateLeadTransferUseCase(ILeadTransferRepository repo) { this.repo = repo; }

    /**
     * Cập nhật LeadTransfer và trả về result.
     * @param cmd dữ liệu cập nhật @return LeadTransferResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public LeadTransferResult execute(UpdateLeadTransferCommand cmd) {
        LeadTransfer existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("LeadTransfer not found: " + cmd.getId()));
        return LeadTransferCommandMapper.toResult(repo.save(LeadTransferCommandMapper.toEntity(cmd, existing)));
    }
}
