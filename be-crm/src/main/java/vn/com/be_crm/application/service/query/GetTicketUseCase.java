package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.mapper.TicketCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy phiếu theo ID. */
public class GetTicketUseCase implements IUseCase<Long, TicketResult> {
    private final ITicketRepository repo;
    /** @param repo port lưu trữ */
    public GetTicketUseCase(ITicketRepository repo) { this.repo = repo; }
    /** Lấy Ticket theo ID. @param id @return TicketResult @throws NotFoundException */
    @Override public TicketResult execute(Long id) {
        return TicketCommandMapper.toResult(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + id)));
    }
}
