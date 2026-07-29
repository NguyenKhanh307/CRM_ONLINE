package vn.com.be_crm.application.activity.dto;

/** Một dòng dữ liệu Activity từ file import. */
public record ImportActivityRowCommand(
        String type,
        String subject,
        String content,
        String priority,
        String targetType,
        Long targetId,
        String relatedType,
        Long relatedId,
        String location,
        String callDirection,
        String callResult,
        Integer callDuration,
        String status,
        String dueAt,
        Long ownerId,
        String ownerEmail
) {}
