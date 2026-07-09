package vn.com.be_crm.application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.service.enums.CommentType;

import java.time.LocalDateTime;

/** Output DTO cho TicketComment. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TicketCommentResult {
    private Long id;
    private Long ticketId;
    private CommentType type;
    private String content;
    private Boolean isInternal;
    private Long authorId;
    private LocalDateTime createdAt;
    // Tên tác giả — do BE resolve (INameResolver).
    private String authorName;
}
