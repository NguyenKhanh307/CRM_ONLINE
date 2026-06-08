package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.CreateLeadAttachmentCommand;
import vn.com.be_crm.application.lead.dto.LeadAttachmentResult;
import vn.com.be_crm.application.lead.mapper.LeadAttachmentCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository;

/** Use case tạo mới tệp đính kèm tiềm năng. */
public class CreateLeadAttachmentUseCase implements IUseCase<CreateLeadAttachmentCommand, LeadAttachmentResult> {
    private final ILeadAttachmentRepository repo;
    /** @param repo port lưu trữ */
    public CreateLeadAttachmentUseCase(ILeadAttachmentRepository repo) { this.repo = repo; }

    /**
     * Tạo mới LeadAttachment và trả về result.
     * @param cmd dữ liệu tạo mới @return LeadAttachmentResult
     */
    @Override
    public LeadAttachmentResult execute(CreateLeadAttachmentCommand cmd) {
        return LeadAttachmentCommandMapper.toResult(repo.save(LeadAttachmentCommandMapper.toEntity(cmd)));
    }
}
