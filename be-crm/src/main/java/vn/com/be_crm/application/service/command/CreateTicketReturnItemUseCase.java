package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.CreateTicketReturnItemCommand;
import vn.com.be_crm.application.service.dto.TicketReturnItemResult;
import vn.com.be_crm.application.service.mapper.TicketReturnItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case tạo mới dòng hàng trả/đổi. */
public class CreateTicketReturnItemUseCase implements IUseCase<CreateTicketReturnItemCommand, TicketReturnItemResult> {
    private final ITicketReturnItemRepository repo;
    private final ITicketRepository ticketRepo;
    /** @param repo port lưu trữ dòng hàng @param ticketRepo port lưu trữ phiếu — kiểm trạng thái đóng */
    public CreateTicketReturnItemUseCase(ITicketReturnItemRepository repo, ITicketRepository ticketRepo) {
        this.repo = repo;
        this.ticketRepo = ticketRepo;
    }
    /** Tạo mới TicketReturnItem. @param cmd @return TicketReturnItemResult */
    @Override public TicketReturnItemResult execute(CreateTicketReturnItemCommand cmd) {
        Ticket ticket = ticketRepo.findById(cmd.getTicketId())
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + cmd.getTicketId()));
        if (ticket.getStatus() == TicketStatus.closed) {
            throw new DomainException("Phiếu đã đóng, không thể thêm dòng hàng. Vui lòng mở lại phiếu trước.");
        }
        return TicketReturnItemCommandMapper.toResult(repo.save(TicketReturnItemCommandMapper.toEntity(cmd)));
    }
}
