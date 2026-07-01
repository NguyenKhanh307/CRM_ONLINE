package vn.com.be_crm.application.service.command;

import vn.com.be_crm.domain.service.entity.TicketComment;
import vn.com.be_crm.domain.service.enums.CommentType;
import vn.com.be_crm.domain.service.repository.ITicketCommentRepository;

/**
 * Use case dùng chung: tự ghi audit log vào ticket_comments (type=system) sau mỗi bước chuyển trạng thái.
 * Tái sử dụng ở TicketWorkflowUseCase và CreateTicketUseCase (pattern giống AddLeadScoreUseCase).
 */
public class LogTicketEventUseCase {
    private final ITicketCommentRepository commentRepo;

    /** @param commentRepo port lưu trữ ghi chú */
    public LogTicketEventUseCase(ITicketCommentRepository commentRepo) { this.commentRepo = commentRepo; }

    /**
     * Ghi một dòng audit log hệ thống (nội bộ) cho phiếu.
     * @param ticketId ID phiếu
     * @param content  nội dung mô tả bước chuyển
     */
    public void execute(Long ticketId, String content) {
        commentRepo.save(TicketComment.builder()
                .ticketId(ticketId).type(CommentType.system).content(content)
                .isInternal(true).authorId(null)
                .build());
    }
}
