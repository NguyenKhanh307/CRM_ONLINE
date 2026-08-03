package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.CreateTicketCommentCommand;
import vn.com.be_crm.application.service.dto.TicketCommentResult;
import vn.com.be_crm.application.service.mapper.TicketCommentMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.entity.TicketComment;
import vn.com.be_crm.domain.service.enums.CommentType;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.repository.ITicketCommentRepository;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case tạo ghi chú (note) do người dùng nhập cho phiếu. */
public class CreateTicketCommentUseCase implements IUseCase<CreateTicketCommentCommand, TicketCommentResult> {
    private final ITicketCommentRepository repo;
    private final ITicketRepository ticketRepo;
    /** @param repo port lưu trữ comment @param ticketRepo port lưu trữ phiếu — kiểm trạng thái đóng */
    public CreateTicketCommentUseCase(ITicketCommentRepository repo, ITicketRepository ticketRepo) {
        this.repo = repo;
        this.ticketRepo = ticketRepo;
    }
    /** Tạo TicketComment type=note. @param cmd @return TicketCommentResult */
    @Override public TicketCommentResult execute(CreateTicketCommentCommand cmd) {
        Ticket ticket = ticketRepo.findById(cmd.getTicketId())
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + cmd.getTicketId()));
        if (ticket.getStatus() == TicketStatus.closed) {
            throw new DomainException("Phiếu đã đóng, không thể ghi chú. Vui lòng mở lại phiếu trước.");
        }
        TicketComment saved = repo.save(TicketComment.builder()
                .ticketId(cmd.getTicketId()).type(CommentType.note).content(cmd.getContent())
                .isInternal(cmd.getIsInternal() == null || cmd.getIsInternal())
                .authorId(cmd.getAuthorId())
                .build());
        return TicketCommentMapper.toResult(saved);
    }
}
