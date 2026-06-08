package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.CreateLeadCommand;
import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

/** Use case tạo mới tiềm năng. */
public class CreateLeadUseCase implements IUseCase<CreateLeadCommand, LeadResult> {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public CreateLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Lead và trả về result.
     * @param cmd dữ liệu tạo mới @return LeadResult
     */
    @Override
    public LeadResult execute(CreateLeadCommand cmd) {
        return LeadCommandMapper.toResult(repo.save(LeadCommandMapper.toEntity(cmd)));
    }
}
