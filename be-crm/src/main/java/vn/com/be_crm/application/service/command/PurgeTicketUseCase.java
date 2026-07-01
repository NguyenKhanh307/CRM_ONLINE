package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

/** Use case xóa vĩnh viễn phiếu khỏi thùng rác (ẩn khỏi danh sách). */
public class PurgeTicketUseCase implements IUseCase<Long, Void> {
    private final ITicketRepository repo;
    /** @param repo port lưu trữ */
    public PurgeTicketUseCase(ITicketRepository repo) { this.repo = repo; }
    /** Ẩn Ticket khỏi thùng rác. @param id @return null */
    @Override public Void execute(Long id) { repo.purgeById(id); return null; }
}
