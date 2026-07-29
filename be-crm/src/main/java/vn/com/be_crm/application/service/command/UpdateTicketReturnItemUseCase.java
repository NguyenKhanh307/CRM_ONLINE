package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.TicketReturnItemResult;
import vn.com.be_crm.application.service.dto.UpdateTicketReturnItemCommand;
import vn.com.be_crm.application.service.mapper.TicketReturnItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng hàng trả/đổi. */
public class UpdateTicketReturnItemUseCase implements IUseCase<UpdateTicketReturnItemCommand, TicketReturnItemResult> {
    private final ITicketReturnItemRepository repo;
    private final ITicketRepository ticketRepo;
    /** @param repo port lưu trữ dòng hàng @param ticketRepo port lưu trữ phiếu — kiểm trạng thái đóng */
    public UpdateTicketReturnItemUseCase(ITicketReturnItemRepository repo, ITicketRepository ticketRepo) {
        this.repo = repo;
        this.ticketRepo = ticketRepo;
    }
    /** Cập nhật TicketReturnItem. @param cmd @return TicketReturnItemResult @throws NotFoundException */
    @Override public TicketReturnItemResult execute(UpdateTicketReturnItemCommand cmd) {
        TicketReturnItem e = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("TicketReturnItem not found: " + cmd.getId()));
        Ticket ticket = ticketRepo.findById(e.getTicketId())
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + e.getTicketId()));
        if (ticket.getStatus() == TicketStatus.closed) {
            throw new DomainException("Phiếu đã đóng, không thể sửa dòng hàng. Vui lòng mở lại phiếu trước.");
        }
        return TicketReturnItemCommandMapper.toResult(repo.save(TicketReturnItemCommandMapper.toEntity(cmd, e)));
    }
}
