package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.dto.UpdateLeadCommand;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật tiềm năng. */
public class UpdateLeadUseCase implements IUseCase<UpdateLeadCommand, LeadResult> {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public UpdateLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Cập nhật Lead và trả về result.
     * @param cmd dữ liệu cập nhật @return LeadResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public LeadResult execute(UpdateLeadCommand cmd) {
        Lead existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Lead not found: " + cmd.getId()));
        return LeadCommandMapper.toResult(repo.save(LeadCommandMapper.toEntity(cmd, existing)));
    }
}
