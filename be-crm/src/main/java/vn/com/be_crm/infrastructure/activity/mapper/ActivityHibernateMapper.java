package vn.com.be_crm.infrastructure.activity.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.activity.entity.Activity;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.infrastructure.shared.audit.AuditStamper;
import vn.com.be_crm.infrastructure.activity.entity.ActivityHibernate;

/**
 * Chuyển đổi giữa Activity domain entity ↔ ActivityHibernate.
 */
@Component
public class ActivityHibernateMapper {

    /**
     * Chuyển domain entity sang Hibernate entity để persist.
     *
     * @param domain Activity domain entity
     * @return ActivityHibernate sẵn sàng lưu DB
     */
    public ActivityHibernate toHibernate(Activity domain) {
        ActivityHibernate h = new ActivityHibernate();
        h.setId(domain.getId());
        h.setType(domain.getType());
        h.setSubject(domain.getSubject());
        h.setContent(domain.getContent());
        h.setPriority(domain.getPriority());
        h.setTargetType(domain.getTargetType());
        h.setTargetId(domain.getTargetId());
        h.setRelatedType(domain.getRelatedType());
        h.setRelatedId(domain.getRelatedId());
        h.setLocation(domain.getLocation());
        h.setCallDirection(domain.getCallDirection());
        h.setCallResult(domain.getCallResult());
        h.setCallDuration(domain.getCallDuration());
        h.setAssignedUserId(domain.getAssignedUserId());
        h.setStatus(domain.getStatus() != null ? domain.getStatus() : ActivityStatus.planned);
        h.setDueAt(domain.getDueAt());
        h.setCompletedAt(domain.getCompletedAt());
        // Đóng dấu người tạo/người sửa (AuditStamper: cần cho body response của PUT)
        return AuditStamper.stamp(h, domain.getCreatedBy(), domain.getUpdatedBy());
    }

    /**
     * Chuyển Hibernate entity sang domain entity sau khi đọc từ DB.
     *
     * @param h ActivityHibernate đọc từ DB
     * @return Activity domain entity
     */
    public Activity toDomain(ActivityHibernate h) {
        return Activity.builder()
                .id(h.getId()).type(h.getType()).subject(h.getSubject())
                .content(h.getContent()).priority(h.getPriority())
                .targetType(h.getTargetType()).targetId(h.getTargetId())
                .relatedType(h.getRelatedType()).relatedId(h.getRelatedId())
                .location(h.getLocation()).callDirection(h.getCallDirection())
                .callResult(h.getCallResult()).callDuration(h.getCallDuration())
                .assignedUserId(h.getAssignedUserId())
                .status(h.getStatus()).dueAt(h.getDueAt())
                .completedAt(h.getCompletedAt()).createdBy(h.getCreatedBy()).updatedBy(h.getUpdatedBy())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt())
                .build();
    }
}
