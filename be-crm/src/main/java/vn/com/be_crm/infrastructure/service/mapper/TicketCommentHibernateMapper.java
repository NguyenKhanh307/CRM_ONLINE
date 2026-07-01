package vn.com.be_crm.infrastructure.service.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.service.entity.TicketComment;
import vn.com.be_crm.infrastructure.service.entity.TicketCommentHibernate;

/** Chuyển đổi giữa TicketComment domain entity ↔ TicketCommentHibernate. */
@Component
public class TicketCommentHibernateMapper {
    /** Chuyển domain entity sang Hibernate entity. @param d @return hibernate entity */
    public TicketCommentHibernate toHibernate(TicketComment d) {
        TicketCommentHibernate h = new TicketCommentHibernate();
        h.setId(d.getId()); h.setTicketId(d.getTicketId()); h.setType(d.getType());
        h.setContent(d.getContent()); h.setInternal(d.isInternal()); h.setAuthorId(d.getAuthorId());
        return h;
    }
    /** Chuyển Hibernate entity sang domain entity. @param h @return domain entity */
    public TicketComment toDomain(TicketCommentHibernate h) {
        return TicketComment.builder()
                .id(h.getId()).ticketId(h.getTicketId()).type(h.getType()).content(h.getContent())
                .isInternal(h.isInternal()).authorId(h.getAuthorId()).createdAt(h.getCreatedAt())
                .build();
    }
}
