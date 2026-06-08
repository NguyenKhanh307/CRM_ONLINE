package vn.com.be_crm.infrastructure.activity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.domain.activity.enums.ActivityType;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng activities.
 */
@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
public class ActivityHibernate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ActivityType type;

    @Column(name = "subject", nullable = false, length = 30)
    private String subject;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ActivityStatus status;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
