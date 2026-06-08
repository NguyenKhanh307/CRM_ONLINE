package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa mềm liên hệ. */
public class DeleteContactUseCase implements IUseCase<Long, Void> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public DeleteContactUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Xóa mềm Contact theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("Contact not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
