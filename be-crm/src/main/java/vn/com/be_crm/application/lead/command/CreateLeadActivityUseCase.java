package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.CreateLeadActivityCommand;
import vn.com.be_crm.application.lead.dto.LeadActivityResult;
import vn.com.be_crm.application.lead.mapper.LeadActivityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadActivityRepository;

/** Use case tạo mới hoạt động tiềm năng. */
public class CreateLeadActivityUseCase implements IUseCase<CreateLeadActivityCommand, LeadActivityResult> {
    private final ILeadActivityRepository repo;
    /** @param repo port lưu trữ */
    public CreateLeadActivityUseCase(ILeadActivityRepository repo) { this.repo = repo; }

    /**
     * Tạo mới LeadActivity và trả về result.
     * @param cmd dữ liệu tạo mới @return LeadActivityResult
     */
    @Override
    public LeadActivityResult execute(CreateLeadActivityCommand cmd) {
        return LeadActivityCommandMapper.toResult(repo.save(LeadActivityCommandMapper.toEntity(cmd)));
    }
}
