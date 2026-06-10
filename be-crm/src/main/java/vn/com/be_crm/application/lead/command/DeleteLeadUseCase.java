package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm tiềm năng. */
public class DeleteLeadUseCase implements IUseCase<DeleteCommand, Void> {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public DeleteLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Xóa mềm Lead theo ID, ghi nhận người xóa.
     * @param cmd command chứa id và deletedBy @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Lead not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy());
        return null;
    }
}
