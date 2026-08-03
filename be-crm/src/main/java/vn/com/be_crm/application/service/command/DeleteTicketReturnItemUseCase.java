package vn.com.be_crm.application.service.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa dòng hàng trả/đổi. */
public class DeleteTicketReturnItemUseCase implements IUseCase<Long, Void> {
    private final ITicketReturnItemRepository repo;
    private final ITicketRepository ticketRepo;
    /** @param repo port lưu trữ dòng hàng @param ticketRepo port lưu trữ phiếu — kiểm trạng thái đóng */
    public DeleteTicketReturnItemUseCase(ITicketReturnItemRepository repo, ITicketRepository ticketRepo) {
        this.repo = repo;
        this.ticketRepo = ticketRepo;
    }
    /** Xóa TicketReturnItem. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        TicketReturnItem e = repo.findById(id).orElseThrow(() -> new NotFoundException("TicketReturnItem not found: " + id));
        Ticket ticket = ticketRepo.findById(e.getTicketId())
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + e.getTicketId()));
        if (ticket.getStatus() == TicketStatus.closed) {
            throw new DomainException("Phiếu đã đóng, không thể xóa dòng hàng. Vui lòng mở lại phiếu trước.");
        }
        repo.deleteById(id); return null;
    }
}
