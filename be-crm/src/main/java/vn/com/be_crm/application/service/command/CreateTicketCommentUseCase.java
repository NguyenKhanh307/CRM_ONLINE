package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.CreateTicketCommentCommand;
import vn.com.be_crm.application.service.dto.TicketCommentResult;
import vn.com.be_crm.application.service.mapper.TicketCommentMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.TicketComment;
import vn.com.be_crm.domain.service.enums.CommentType;
import vn.com.be_crm.domain.service.repository.ITicketCommentRepository;

/** Use case tạo ghi chú (note) do người dùng nhập cho phiếu. */
public class CreateTicketCommentUseCase implements IUseCase<CreateTicketCommentCommand, TicketCommentResult> {
    private final ITicketCommentRepository repo;
    /** @param repo port lưu trữ */
    public CreateTicketCommentUseCase(ITicketCommentRepository repo) { this.repo = repo; }
    /** Tạo TicketComment type=note. @param cmd @return TicketCommentResult */
    @Override public TicketCommentResult execute(CreateTicketCommentCommand cmd) {
        TicketComment saved = repo.save(TicketComment.builder()
                .ticketId(cmd.getTicketId()).type(CommentType.note).content(cmd.getContent())
                .isInternal(cmd.getIsInternal() == null || cmd.getIsInternal())
                .authorId(cmd.getAuthorId())
                .build());
        return TicketCommentMapper.toResult(saved);
    }
}
