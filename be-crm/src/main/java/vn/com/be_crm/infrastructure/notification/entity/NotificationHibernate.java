package vn.com.be_crm.infrastructure.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng notifications.
 */
@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor
public class NotificationHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;
    @Column(name = "type", length = 30)
    private String type;
    @Column(name = "title", length = 150)
    private String title;
    @Column(name = "content", length = 500)
    private String content;
    @Column(name = "lead_id")
    private Long leadId;
    @Column(name = "target_id")
    private Long targetId;
    @Column(name = "is_read", nullable = false)
    private boolean isRead;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
