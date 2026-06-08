package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadAttachmentResult;
import vn.com.be_crm.application.lead.dto.UpdateLeadAttachmentCommand;
import vn.com.be_crm.application.lead.mapper.LeadAttachmentCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.entity.LeadAttachment;
import vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật tệp đính kèm tiềm năng. */
public class UpdateLeadAttachmentUseCase implements IUseCase<UpdateLeadAttachmentCommand, LeadAttachmentResult> {
    private final ILeadAttachmentRepository repo;
    /** @param repo port lưu trữ */
    public UpdateLeadAttachmentUseCase(ILeadAttachmentRepository repo) { this.repo = repo; }

    /**
     * Cập nhật LeadAttachment và trả về result.
     * @param cmd dữ liệu cập nhật @return LeadAttachmentResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public LeadAttachmentResult execute(UpdateLeadAttachmentCommand cmd) {
        LeadAttachment existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("LeadAttachment not found: " + cmd.getId()));
        return LeadAttachmentCommandMapper.toResult(repo.save(LeadAttachmentCommandMapper.toEntity(cmd, existing)));
    }
}
