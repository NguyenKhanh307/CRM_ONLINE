package vn.com.be_crm.application.activity.dto;

/** Một dòng dữ liệu Activity từ file import. */
public record ImportActivityRowCommand(
        String type,
        String subject,
        String content,
        String status,
        String dueAt,
        Long ownerId,
        String ownerEmail
) {}
