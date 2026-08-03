package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa mềm liên hệ. */
public class DeleteContactUseCase implements IUseCase<DeleteCommand, Void> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public DeleteContactUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Xóa mềm Contact theo ID, ghi nhận người xóa.
     * @param cmd command chứa id và deletedBy @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Contact not found: " + cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy());
        return null;
    }
}
