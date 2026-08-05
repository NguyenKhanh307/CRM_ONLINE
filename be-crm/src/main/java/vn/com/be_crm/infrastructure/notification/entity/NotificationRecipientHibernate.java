package vn.com.be_crm.infrastructure.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// ánh xạ bảng notification_recipients — trạng thái đọc/xóa của một người nhận với một thông báo
@Entity
@Table(name = "notification_recipients")
@Getter @Setter @NoArgsConstructor
public class NotificationRecipientHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;
    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;
    @Column(name = "is_read", nullable = false)
    private boolean isRead;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
