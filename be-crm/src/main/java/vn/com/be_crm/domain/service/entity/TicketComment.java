package vn.com.be_crm.domain.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.CommentType;

import java.time.LocalDateTime;

/**
 * Domain entity ghi chú / lịch sử ticket (ticket_comments).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketComment {
    /** ID ghi chú. */
    private Long id;
    /** ID phiếu. */
    private Long ticketId;
    /** Loại: system (audit tự sinh) | note (người nhập). */
    private CommentType type;
    /** Nội dung. */
    private String content;
    /** True = nội bộ, false = gửi khách. */
    private boolean isInternal;
    /** ID người viết (null nếu system tự sinh). */
    private Long authorId;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
}
