package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa tệp đính kèm tiềm năng. */
public class DeleteLeadAttachmentUseCase implements IUseCase<Long, Void> {
    private final ILeadAttachmentRepository repo;
    /** @param repo port lưu trữ */
    public DeleteLeadAttachmentUseCase(ILeadAttachmentRepository repo) { this.repo = repo; }

    /**
     * Xóa LeadAttachment theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("LeadAttachment not found: " + id));
        repo.deleteById(id); return null;
    }
}
