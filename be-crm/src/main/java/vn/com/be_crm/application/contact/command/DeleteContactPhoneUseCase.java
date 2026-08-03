package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactPhoneRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa số điện thoại liên hệ. */
public class DeleteContactPhoneUseCase implements IUseCase<Long, Void> {
    private final IContactPhoneRepository repo;
    /** @param repo port lưu trữ */
    public DeleteContactPhoneUseCase(IContactPhoneRepository repo) { this.repo = repo; }

    /**
     * Xóa ContactPhone theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("ContactPhone not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
