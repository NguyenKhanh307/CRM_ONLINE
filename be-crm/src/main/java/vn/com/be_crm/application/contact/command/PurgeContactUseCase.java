package vn.com.be_crm.application.contact.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

/** Use case ẩn liên hệ khỏi thùng rác. */
public class PurgeContactUseCase implements IUseCase<Long, Void> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public PurgeContactUseCase(IContactRepository repo) { this.repo = repo; }
    /** Set is_purged = true. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
