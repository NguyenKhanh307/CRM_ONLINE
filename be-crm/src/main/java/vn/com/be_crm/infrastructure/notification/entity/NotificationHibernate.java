package vn.com.be_crm.infrastructure.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// ánh xạ bảng notifications (nội dung dùng chung — xem NotificationRecipientHibernate cho
// trạng thái theo từng người nhận)
@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor
public class NotificationHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type", length = 30)
    private String type;
    @Column(name = "title", length = 150)
    private String title;
    @Column(name = "content", length = 500)
    private String content;
    @Column(name = "target_type", length = 50)
    private String targetType;
    @Column(name = "target_id")
    private Long targetId;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
