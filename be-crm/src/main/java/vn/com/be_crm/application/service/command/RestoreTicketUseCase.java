package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

/** Use case khôi phục phiếu từ thùng rác. */
public class RestoreTicketUseCase implements IUseCase<Long, Void> {
    private final ITicketRepository repo;
    /** @param repo port lưu trữ */
    public RestoreTicketUseCase(ITicketRepository repo) { this.repo = repo; }
    /** Khôi phục Ticket. @param id @return null */
    @Override public Void execute(Long id) { repo.restoreById(id); return null; }
}
