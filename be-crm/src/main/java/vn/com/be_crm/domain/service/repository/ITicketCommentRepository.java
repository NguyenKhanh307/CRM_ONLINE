package vn.com.be_crm.domain.service.repository;

import vn.com.be_crm.domain.service.entity.TicketComment;

import java.util.List;

/**
 * Port lưu trữ cho TicketComment (ghi chú / lịch sử ticket).
 */
public interface ITicketCommentRepository {
    /** Lưu mới ghi chú ticket. @param comment @return entity sau khi lưu */
    TicketComment save(TicketComment comment);
    /** Lấy danh sách ghi chú theo ticketId (cũ → mới). @param ticketId @return danh sách */
    List<TicketComment> findAllByTicketId(Long ticketId);
}
