package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadActivityResult;
import vn.com.be_crm.application.lead.dto.UpdateLeadActivityCommand;
import vn.com.be_crm.application.lead.mapper.LeadActivityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.entity.LeadActivity;
import vn.com.be_crm.domain.lead.repository.ILeadActivityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật hoạt động tiềm năng. */
public class UpdateLeadActivityUseCase implements IUseCase<UpdateLeadActivityCommand, LeadActivityResult> {
    private final ILeadActivityRepository repo;
    /** @param repo port lưu trữ */
    public UpdateLeadActivityUseCase(ILeadActivityRepository repo) { this.repo = repo; }

    /**
     * Cập nhật LeadActivity và trả về result.
     * @param cmd dữ liệu cập nhật @return LeadActivityResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public LeadActivityResult execute(UpdateLeadActivityCommand cmd) {
        LeadActivity existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("LeadActivity not found: " + cmd.getId()));
        return LeadActivityCommandMapper.toResult(repo.save(LeadActivityCommandMapper.toEntity(cmd, existing)));
    }
}
