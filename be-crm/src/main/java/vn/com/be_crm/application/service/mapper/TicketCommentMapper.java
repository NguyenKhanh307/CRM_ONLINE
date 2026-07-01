package vn.com.be_crm.application.service.mapper;

import vn.com.be_crm.application.service.dto.TicketCommentResult;
import vn.com.be_crm.domain.service.entity.TicketComment;

/** Chuyển đổi TicketComment ↔ TicketCommentResult. */
public class TicketCommentMapper {

    /**
     * Chuyển TicketComment sang TicketCommentResult.
     * @param e domain entity @return result DTO
     */
    public static TicketCommentResult toResult(TicketComment e) {
        return TicketCommentResult.builder()
                .id(e.getId()).ticketId(e.getTicketId()).type(e.getType())
                .content(e.getContent()).isInternal(e.isInternal()).authorId(e.getAuthorId())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private TicketCommentMapper() {}
}
