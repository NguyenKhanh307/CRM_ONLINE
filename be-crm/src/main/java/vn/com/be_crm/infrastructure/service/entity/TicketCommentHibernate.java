package vn.com.be_crm.infrastructure.service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import vn.com.be_crm.domain.service.enums.CommentType;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng ticket_comments.
 */
@Entity
@Table(name = "ticket_comments")
@Getter @Setter @NoArgsConstructor
public class TicketCommentHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ticket_id", nullable = false) private Long ticketId;
    @Enumerated(EnumType.STRING) @Column(name = "type", length = 10) private CommentType type;
    @Column(name = "content", nullable = false, length = 1000) private String content;
    @Column(name = "is_internal") private boolean isInternal;
    @Column(name = "author_id") private Long authorId;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}
