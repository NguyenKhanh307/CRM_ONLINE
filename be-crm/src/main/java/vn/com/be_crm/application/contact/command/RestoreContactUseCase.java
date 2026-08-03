package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

/** Use case khôi phục liên hệ từ thùng rác. */
public class RestoreContactUseCase implements IUseCase<Long, Void> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public RestoreContactUseCase(IContactRepository repo) { this.repo = repo; }
    /** Khôi phục Contact. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
