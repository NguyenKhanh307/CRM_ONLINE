package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.CreateLeadCommand;
import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

// tạo mới tiềm năng
public class CreateLeadUseCase implements IUseCase<CreateLeadCommand, LeadResult> {
    private final ILeadRepository repo;

    public CreateLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    @Override
    public LeadResult execute(CreateLeadCommand cmd) {
        // check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã tiềm năng \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        return LeadCommandMapper.toResult(repo.save(LeadCommandMapper.toEntity(cmd)));
    }
}
