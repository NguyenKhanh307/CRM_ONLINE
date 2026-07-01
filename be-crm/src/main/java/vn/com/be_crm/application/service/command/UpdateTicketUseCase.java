package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.dto.UpdateTicketCommand;
import vn.com.be_crm.application.service.mapper.TicketCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật thông tin phiếu (không đổi trạng thái). */
public class UpdateTicketUseCase implements IUseCase<UpdateTicketCommand, TicketResult> {
    private final ITicketRepository repo;
    /** @param repo port lưu trữ */
    public UpdateTicketUseCase(ITicketRepository repo) { this.repo = repo; }
    /** Cập nhật Ticket. @param cmd @return TicketResult @throws NotFoundException */
    @Override public TicketResult execute(UpdateTicketCommand cmd) {
        Ticket e = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + cmd.getId()));
        return TicketCommandMapper.toResult(repo.save(TicketCommandMapper.toEntity(cmd, e)));
    }
}
